.PHONY: test clean compile
.DEFAULT_GOAL: test

matrix: compile
	cd target/classes && java ca.mcgill.ecse420.a3.MatrixVectorMultiplication

compile:
	mvn -q clean compile

clean:
	mvn -q clean

test:
	mvn -q clean test

