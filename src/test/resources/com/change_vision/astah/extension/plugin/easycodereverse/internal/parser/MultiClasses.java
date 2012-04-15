package com.change_vision.astah.extension.plugin.easycodereverse.internal.parser;

public class MultiClassInFile {
	public void doSomething() {
	}
	
	class InnerClass {
		public int incr() {
			return 1;
		}
	}
}

class HogeImp extends HogeParent implements Hoge, Fuga {
	public String say() {
		return "hello";
	}

	@Override
	public void sayHello(int x) {
		say();
	}

	@Override
	public void sayGoodBye() {
	}
}

interface Hoge {
	void sayHello(int x);
}

interface Fuga {
	void sayGoodBye();
}

enum EnumInFile {
	HOGE, FUGA;
}

class HogeParent {
}