.PHONY: test run clean multiplication compile philosophers
.DEFAULT_GOAL: test

multiplication: compile
	cd target/classes && java ca.mcgill.ecse420.a1.MatrixMultiplication

deadlock: compile
	cd target/classes && java ca.mcgill.ecse420.a1.Deadlock

philosophers: compile
	cd target/classes && java ca.mcgill.ecse420.a1.DiningPhilosophers

compile:
	mvn -q clean compile

clean:
	mvn -q clean

test:
	mvn -q clean test

