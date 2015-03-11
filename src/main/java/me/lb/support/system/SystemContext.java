package me.lb.support.system;

public class SystemContext {
	
	private static ThreadLocal<Integer> offsetThread = new ThreadLocal<Integer>();
	private static ThreadLocal<Integer> pageSizeThread = new ThreadLocal<Integer>();

	public static int getOffset() {
		Integer offset = (Integer) offsetThread.get();
		if (offset == null) {
			return 0;
		} else {
			return offset.intValue();
		}
	}

	public static void setOffset(int offset) {
		offsetThread.set(offset);
	}

	public static void removeOffset() {
		offsetThread.remove();
	}

	public static int getPageSize() {
		Integer pageSize = pageSizeThread.get();
		if (pageSize == null) {
			return Integer.MAX_VALUE;
		} else {
			return pageSize.intValue();
		}
	}

	public static void setPageSize(int pageSize) {
		pageSizeThread.set(pageSize);
	}

	public static void removePageSize() {
		pageSizeThread.remove();
	}
	
}
