.PHONY: test run clean multiplication compile philosophers
.DEFAULT_GOAL: test

multiplication: compile
	cd target/classes && java ca.mcgill.ecse420.a1.MatrixMultiplication

deadlock: compile
	cd target/classes && java ca.mcgill.ecse420.a1.Deadlock

philosophersDeadlock: compile
	cd target/classes && java ca.mcgill.ecse420.a1.DiningPhilosophersDeadlock

philosophersDeadlockFree: compile
	cd target/classes && java ca.mcgill.ecse420.a1.DiningPhilosophersDeadlockFree

philosophersFair: compile
	cd target/classes && java ca.mcgill.ecse420.a1.DiningPhilosophersFair

compile:
	mvn -q clean compile

clean:
	mvn -q clean

test:
	mvn -q clean test

