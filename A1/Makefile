.PHONY: test run clean
.DEFAULT_GOAL: run

run:
	mvn -q clean compile
	cd target/classes && java ca.mcgill.ecse420.a1.MatrixMultiplication

clean:
	mvn -q clean

test:
	mvn -q clean test

