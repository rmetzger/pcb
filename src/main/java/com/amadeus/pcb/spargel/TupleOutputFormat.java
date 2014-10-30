package com.amadeus.pcb.spargel;

import org.apache.flink.api.common.io.BlockInfo;
import org.apache.flink.api.common.io.FileOutputFormat;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.core.memory.OutputViewDataOutputStreamWrapper;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

;

@SuppressWarnings("serial")
public abstract class TupleOutputFormat<T extends Tuple> extends FileOutputFormat<T> {

	/**
	 * The config parameter which defines the fixed length of a record.
	 */
	public static final String BLOCK_SIZE_PARAMETER_KEY = "output.block_size";

	public static final long NATIVE_BLOCK_SIZE = Long.MIN_VALUE;

	/**
	 * The block size to use.
	 */
	private long blockSize = NATIVE_BLOCK_SIZE;

	private DataOutputStream dataOutputStream;

	private BlockBasedOutput blockBasedInput;

	public TupleOutputFormat(Path path) {
		super(path);
	}

	public TupleOutputFormat() {}

	@Override
	public void close() throws IOException {
		this.dataOutputStream.close();
		super.close();
	}
	
	protected void complementBlockInfo(BlockInfo blockInfo) throws IOException {
	}

	@Override
	public void configure(Configuration parameters) {
		super.configure(parameters);

		// read own parameters
		this.blockSize = parameters.getLong(BLOCK_SIZE_PARAMETER_KEY, NATIVE_BLOCK_SIZE);
		if (this.blockSize < 1 && this.blockSize != NATIVE_BLOCK_SIZE) {
			throw new IllegalArgumentException("The block size parameter must be set and larger than 0.");
		}
		if (this.blockSize > Integer.MAX_VALUE) {
			throw new UnsupportedOperationException("Currently only block size up to Integer.MAX_VALUE are supported");
		}
	}

	protected BlockInfo createBlockInfo() {
		return new BlockInfo();
	}

	@Override
	public void open(int taskNumber, int numTasks) throws IOException {
		super.open(taskNumber, numTasks);

		final long blockSize = this.blockSize == NATIVE_BLOCK_SIZE ?
			this.outputFilePath.getFileSystem().getDefaultBlockSize() : this.blockSize;

		this.blockBasedInput = new BlockBasedOutput(this.stream, (int) blockSize);
		this.dataOutputStream = new DataOutputStream(this.blockBasedInput);
	}

	@Override
	public void writeRecord(T record) throws IOException {
		this.blockBasedInput.startRecord();
		this.serialize(record, new OutputViewDataOutputStreamWrapper(this.dataOutputStream));
	}
	
	protected abstract void serialize(T record, DataOutputView dataOutput) throws IOException; 

	/**
	 * Writes a block info at the end of the blocks.<br>
	 * Current implementation uses only int and not long.
	 * 
	 */
	protected class BlockBasedOutput extends FilterOutputStream {

		private static final int NO_RECORD = -1;

		private final int maxPayloadSize;

		private int blockPos;

		private int blockCount, totalCount;

		private long firstRecordStartPos = NO_RECORD;

		private BlockInfo blockInfo = TupleOutputFormat.this.createBlockInfo();

		private DataOutputStream headerStream;

		public BlockBasedOutput(OutputStream out, int blockSize) {
			super(out);
			this.headerStream = new DataOutputStream(out);
			this.maxPayloadSize = blockSize - this.blockInfo.getInfoSize();
		}

		@Override
		public void close() throws IOException {
			if (this.blockPos > 0) {
				this.writeInfo();
			}
			super.flush();
			super.close();
		}

		public void startRecord() {
			if (this.firstRecordStartPos == NO_RECORD) {
				this.firstRecordStartPos = this.blockPos;
			}
			this.blockCount++;
			this.totalCount++;
		}

		@Override
		public void write(byte[] b) throws IOException {
			this.write(b, 0, b.length);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {

			for (int remainingLength = len, offset = off; remainingLength > 0;) {
				int blockLen = Math.min(remainingLength, this.maxPayloadSize - this.blockPos);
				this.out.write(b, offset, blockLen);

				this.blockPos += blockLen;
				if (this.blockPos >= this.maxPayloadSize) {
					this.writeInfo();
				}
				remainingLength -= blockLen;
				offset += blockLen;
			}
		}

		@Override
		public void write(int b) throws IOException {
			super.write(b);
			if (++this.blockPos >= this.maxPayloadSize) {
				this.writeInfo();
			}
		}

		private void writeInfo() throws IOException {
			this.blockInfo.setRecordCount(this.blockCount);
			this.blockInfo.setAccumulatedRecordCount(this.totalCount);
			this.blockInfo.setFirstRecordStart(this.firstRecordStartPos == NO_RECORD ? 0 : this.firstRecordStartPos);
			TupleOutputFormat.this.complementBlockInfo(this.blockInfo);
			this.blockInfo.write(new OutputViewDataOutputStreamWrapper(this.headerStream));
			this.blockPos = 0;
			this.blockCount = 0;
			this.firstRecordStartPos = NO_RECORD;
		}
	}

}
