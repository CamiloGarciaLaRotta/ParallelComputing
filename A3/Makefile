.PHONY: test clean compile
.DEFAULT_GOAL: test

mutex: compile
	cd target/classes && java ca.mcgill.ecse420.a2.MutexValidator

compile:
	mvn -q clean compile

clean:
	mvn -q clean

test:
	mvn -q clean test

