.PHONY: test clean compile
.DEFAULT_GOAL: test

filter: compile
	cd target/classes && java ca.mcgill.ecse420.a2.FilterLock

bakery: compile
	cd target/classes && java ca.mcgill.ecse420.a2.BakeryLock

compile:
	mvn -q clean compile

clean:
	mvn -q clean

test:
	mvn -q clean test

