package com.tomosware.cylib.calc;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 
 * 用來處理計算機的核心，如果做得好，直接拿到 TmsCyLib 那邊，改掉所有計算機的核心計算
 * 
 * 主要是接受各種 operand and operator 的 input ，然後可以隨時呼叫 evaluate 吐出結果
 * 
 * @author ericlin
 *
 */
public class Calc {

	public static final Double INVALID_VALUE = Double.MIN_VALUE;
	public static final OpType INVALID_OP = OpType.Invalid;

	OpType m_latestOp = INVALID_OP;
	Double m_latestValue = INVALID_VALUE;
    Double m_lastEval = INVALID_VALUE;

    Deque<OpType> m_opStack = new LinkedList<>();
    Deque<Double> m_valueStack = new LinkedList<>();

    //	For Undo
    Deque<OpType> m_opUndoStack = new LinkedList<>();
    Deque<Double> m_valueUndoStack = new LinkedList<>();
    boolean m_bOpEvaled = false;

    public void clear() {
        m_opStack.clear();
        m_valueStack.clear();

        m_opUndoStack.clear();
        m_valueUndoStack.clear();
        
        m_latestOp = INVALID_OP;
        m_latestValue = INVALID_VALUE;
        m_lastEval = INVALID_VALUE;
    }

    public Double evaluate() {
    	if (m_opStack.size() == 0) {
    		
    		if (canRedo())
    			redoLatest();
    		else
				return INVALID_VALUE;
    	}

        while (m_opStack.size() > 0)
            doOp(m_opStack, m_valueStack);

        if (m_valueStack.size() > 0)
        	m_lastEval = m_valueStack.pop();
        
        //	Clear all undo state
        m_valueUndoStack.clear();
        m_opUndoStack.clear();
        m_bOpEvaled = false;
        
        return m_lastEval;
    }

    public Calc number(Double value) {
    	m_latestValue = value;
        numberNoKeep(value);
        return this;
    }

    //	For redo
    public Calc numberNoKeep(Double value) {
        m_valueStack.push(value);
    	return this;
    }

    public void doOp(OpType op) {
    	m_latestOp = op;
    	doOpNoKeep(op);
    }

    public Double undoOp() {
        Double lastValue = INVALID_VALUE;

    	if (m_opStack.size() > 0)
    		m_opStack.pop();

    	if (m_valueStack.size() > 0)
    		m_valueStack.pop();
    	
    	//	這邊會假設一定是一個 op 對應兩個 value
    	if (m_bOpEvaled && m_valueUndoStack.size() > 1 && m_opUndoStack.size() > 0) {
    		m_opStack.push(m_opUndoStack.pop());
    		m_valueStack.push(m_valueUndoStack.pop());
    		m_valueStack.push(m_valueUndoStack.pop());
    		
    		lastValue = m_valueStack.pop();
    	}
    	
    	if (m_opStack.size() > 0)
    		m_latestOp = m_opStack.peekFirst();
    	else
			m_latestOp = INVALID_OP;

    	return lastValue;
    }

    public void doOpNoKeep(OpType op) {
        doOp(op, m_opStack, m_valueStack);
    }

    public Double getTopValue() {
    	if (m_valueStack.size() > 0)
    		return m_valueStack.peekFirst();
    		
		return 0.0d;
    }

    public OpType getTopOp() {
    	if (m_opStack.size() > 0)
    		return m_opStack.peekFirst();
    	
    	return OpType.OpNone;
    }

    public void redoLatest() {
    
    	//	Clear State --> Do nothing for ReDo
    	if (m_lastEval.equals(INVALID_VALUE)) return;
    	if (m_latestOp == INVALID_OP) return;
    	if (m_latestValue.equals(INVALID_VALUE)) return;
    
    	if (m_valueStack.size() == 0)
    		m_valueStack.push(m_lastEval);

    	numberNoKeep(m_latestValue);
    	doOpNoKeep(m_latestOp);
    }

    public boolean isEvaled() {
    	return m_opStack.size() == 0;
    }
    
    public boolean isOpEmpty() {
    	return m_opStack.size() == 0;
    }

    public void setLastEval(Double nValue) {
    	m_lastEval = nValue;
    }

    public boolean canRedo() {
    	return !m_latestValue.equals(INVALID_VALUE) && m_latestOp != INVALID_OP;
    }

    //  ==================================================================================
    //  Private methods

    private static int prior(OpType type){
        switch(type){
            case OpAdd:
            case OpSub: return 1;
            case OpMul:
            case OpDiv: return 2;
        }
        return 0;
    }

    private static Double evaluate(Double a, Double b, OpType op){
        switch(op){
            case OpAdd: return a + b;
            case OpSub: return a - b;
            case OpDiv: return a / b;
            case OpMul: return a * b;
        }
        return 0.0d;
    }

    private void doOp(Deque<OpType> opStack, Deque<Double> vStack){
        Double b = vStack.isEmpty() ? 0.0d : vStack.pop();
        m_valueUndoStack.push(b);

        Double a = vStack.isEmpty() ? 0.0d : vStack.pop();
        m_valueUndoStack.push(a);

        OpType op = opStack.pop();
        m_opUndoStack.push(op);

        Double res = evaluate(a, b, op);
        vStack.push(res);

        m_bOpEvaled = true;
    }

    private void doOp(OpType op, Deque<OpType> opStack, Deque<Double> vStack) {

        while (!opStack.isEmpty() && prior(op) <= prior(opStack.peekFirst()))
            doOp(opStack, vStack);

        opStack.push(op);
    }

}

