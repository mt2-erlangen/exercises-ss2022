# Just file: https://github.com/casey/just
build:
	./gradlew build

run: 
	./gradlew run
    
project_main:
	./gradlew run -PmainClass=project.ProjectMain

backproject_main:
	./gradlew run -PmainClass=project.BackProjectMain

debug-example:
	./gradlew run -PmainClass=exercises.DebugExample

ex0:
	./gradlew run -PmainClass=exercises.Exercise00

ex1:
	./gradlew run -PmainClass=exercises.Exercise01

ex2:
	./gradlew run -PmainClass=exercises.Exercise02

ex3:
	./gradlew run -PmainClass=exercises.Exercise03

ex3_demo:
	./gradlew run -PmainClass=exercises.Exercise03Demo

ex4:
	./gradlew run -PmainClass=exercises.Exercise04

ex5:
	./gradlew run -PmainClass=exercises.Exercise05

ex5_demo:
	./gradlew run -PmainClass=exercises.Exercise05Demo

ex6:
	./gradlew run -PmainClass=exercises.Exercise06

clean:
	./gradlew clean

