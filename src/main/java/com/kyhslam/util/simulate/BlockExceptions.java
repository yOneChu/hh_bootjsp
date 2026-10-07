package com.kyhslam.util.simulate;

/**
 * 블록 시뮬레이션 전용 예외 (PID 실행 공통 예외는 PidExceptions 사용).
 * variant_errorlog.EXCEPTION_TYPE 에 getSimpleName() 이 쓰이므로 원본 클래스명과 동일하게 유지한다.
 */
public class BlockExceptions {

	private BlockExceptions() {}

	/** dyna.plmetc.exception.HdelBusinessException */
	public static class HdelBusinessException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public HdelBusinessException(String message) {
			super(message);
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
