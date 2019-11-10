// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

@KBD
D=A 

@n 
M=D

@SCREEN
D=A

@n 
M=M-D // get the number of RAMs to be manipulated

(LOOP)
@i 
M=0

@SCREEN
D=A
@pixel
M=D  // set i and use pixel as a pointer to the RAM being manipulated
     // initially pixel equals SCREEN

@KBD
D=M
@BLACK
D;JGT // listen to the keybourd, when a key is pressed, jump to the BLACK branch
      // otherwise enter to the WHITE branch  

(WHITE)
@i 
D=M
@n
D=D-M
@LOOP
D;JEQ  // if i==n, break

@pixel
D=M
@i
A=D+M
M=0  //set the RAM unit to 0, which is all white

@i
M=M+1 //i++

@WHITE
0;JMP

(BLACK)
@i 
D=M
@n
D=D-M
@LOOP
D;JEQ

@pixel
D=M
@i
A=D+M
M=-1  // -1 == 1111 1111 1111 1111, all black

@i
M=M+1

@BLACK
0;JMP

@LOOP
0;JMP

