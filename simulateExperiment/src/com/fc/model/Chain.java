package com.fc.model;

import java.util.List;

import com.fc.tuple.CorpTupleWithTestCase;
import com.fc.tuple.Tuple;

public class Chain extends CharacterWM {

	private List<Tuple> currentChain;
	private int head;
	private int tail;
	private int middle;

	public Chain(TuplePool pool, CorpTupleWithTestCase generate) {
		super(pool, generate);
		// TODO Auto-generated constructor stub
		reset();

	}

	@Override
	protected void inital() {
		reset();
	}

	@Override
	protected void extraDealAfterBug(Tuple tuple) {
		head = middle + 1;
		if (head > tail) {
			this.pool.getExistedBugTuples().add(tuple);
			this.pool.compress(this.pool.getExistedBugTuples());
			int last = middle + 1;
			if (last < this.currentChain.size()) {
				this.pool.getExistedRightTuples().add(currentChain.get(last));
				this.pool.compress_r(this.pool.getExistedRightTuples());
			}
			this.reset();
		} else
			middle = (head + tail) / 2;
	}

	@Override
	protected void extraDealAfterRight(Tuple tuple) {
		tail = middle - 1;
		if (tail < head) {
			this.pool.getExistedRightTuples().add(tuple);
			this.pool.compress_r(this.pool.getExistedRightTuples());
			int last = middle - 1;
			if (last >= 0) {
				this.pool.getExistedBugTuples().add(currentChain.get(last));
				this.pool.compress(this.pool.getExistedBugTuples());
			}
			this.reset();
		} else
			middle = (head + tail) / 2;
	}

	@Override
	protected Tuple seletctTupleUnderTest() {
		if (currentChain == null)
			return null;
		return currentChain.get(middle);
	}

	public void reset() {
		System.out.println("start");
		this.currentChain = pool.getLongestPath();
		System.out.println("end");
		this.head = 0;
		this.middle = 0;
		if (currentChain != null)
			this.tail = currentChain.size() - 1;
		else
			this.tail = -1;
	}

}
