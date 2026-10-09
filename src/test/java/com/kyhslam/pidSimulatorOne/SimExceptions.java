package com.kyhslam.pidSimulatorOne;

/**
 * 블록 시뮬레이션 예외 모음.
 * variant_errorlog.EXCEPTION_TYPE 에 getSimpleName() 이 쓰이므로 원본 클래스명과 동일하게 유지한다.
 */
public class SimExceptions {

	private SimExceptions() {}

	/** dyna.plmetc.exception.HdelBusinessException */
	public static class HdelBusinessException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public HdelBusinessException(String message) {
			super(message);
		}
	}

	/** dyna.plmetc.variant.PidNotFoundException */
	public static class PidNotFoundException extends Exception {
		private static final long serialVersionUID = 1L;
		public PidNotFoundException(String pid) {
			super(pid);
		}
	}

	/** dyna.plmetc.variant.PidInfiniteLoopException */
	public static class PidInfiniteLoopException extends Exception {
		private static final long serialVersionUID = 1L;
		public PidInfiniteLoopException(String pid) {
			super(pid);
		}
	}

	/** dyna.plmetc.exception.NumberComparingException */
	public static class NumberComparingException extends RuntimeException {
		private static final long serialVersionUID = -3359678128595546485L;
		private final String specValue;
		private final String condition;

		public NumberComparingException(String specValue, String condition) {
			super();
			this.specValue = specValue;
			this.condition = condition;
		}

		public String getSpecValue() {
			return specValue;
		}

		public String getCondition() {
			return condition;
		}
	}

	/** dyna.plmetc.util.CalculatorException */
	public static class CalculatorException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public CalculatorException(String input, RuntimeException e) {
			super("수식정의 잘못됨 : " + input, e);
		}
	}

	/** dyna.plmetc.bom.IllegalConditionDefinitionException */
	public static class IllegalConditionDefinitionException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public IllegalConditionDefinitionException(String s) {
			super("조건절 정의 잘못됨 : " + s);
		}
	}

	/** dyna.plmetc.exception.InputValueUnvalidException */
	public static class InputValueUnvalidException extends Exception {
		private static final long serialVersionUID = 1L;
		public InputValueUnvalidException(String message) {
			super(message);
		}
	}
}
