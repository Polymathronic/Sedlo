/*
Testing Ver2.0
Extracts COP coordinates from pressure mat.
*/

//output pins
int outputPin1 = 16;   // 1st row
int outputPin2 = 15;   // 2nd row
int outputPin3 = 19;   // 3rd row

//input pins
int inputPin1 = A8;   //1st column
int inputPin2 = A7;   //2nd column
int inputPin3 = A6;   //3rd column

//input voltage (x,y = row,column)
float inVoltage11 = 0.0;  //FSRs in 1st row
float inVoltage12 = 0.0;
float inVoltage13 = 0.0;
float inVoltage21 = 0.0;  //FSRs in 2nd row
float inVoltage22 = 0.0;
float inVoltage23 = 0.0;
float inVoltage31 = 0.0;  //DSRs in 3rd row
float inVoltage32 = 0.0;
float inVoltage33 = 0.0;

//FSR coordinates
float x31 = 5.5;  float y31 = 5.5;
float x21 = 5.5;  float y21 = 15.0;
float x11 = 5.5;  float y11 = 24.5;
float x32 = 15.0; float y32 = 5.5;
float x22 = 15.0; float y22 = 15.0;
float x12 = 15.0; float y12 = 24.5;
float x33 = 24.5; float y33 = 5.5;
float x23 = 24.5; float y23 = 15.0;
float x13 = 24.5; float y13 = 24.5;
 
//params for COP calculation
float voltageX = 0.0;
float voltageY = 0.0;
float voltageSum = 0.0;
float copX = 0.0;
float copY = 0.0;
 
//initialization of pins
void setup() {
  //output pins init
  pinMode(outputPin1, OUTPUT);
  pinMode(outputPin2, OUTPUT);
  pinMode(outputPin3, OUTPUT); 
  
  //serial communication at 9600b/s
  Serial.begin(9600);
}
 
//COP calculation
void loop() {
  //measure FSRs in 1st row
  digitalWrite(outputPin1, HIGH);
  inVoltage11 = analogRead(inputPin1) * (3.3 / 1023.0); //TODO: 1023 max???
  inVoltage12 = analogRead(inputPin2) * (3.3 / 1023.0);
  inVoltage13 = analogRead(inputPin3) * (3.3 / 1023.0);
  digitalWrite(outputPin1, LOW);

  //measure 2nd column
  digitalWrite(outputPin2, HIGH);
  inVoltage21 = analogRead(inputPin1) * (3.3 / 1023.0);
  inVoltage22 = analogRead(inputPin2) * (3.3 / 1023.0);
  inVoltage23 = analogRead(inputPin3) * (3.3 / 1023.0);
  digitalWrite(outputPin2, LOW);
  
  //measure 3rd column
  digitalWrite(outputPin3, HIGH);
  inVoltage31 = analogRead(inputPin1) * (3.3 / 1023.0);
  inVoltage32 = analogRead(inputPin2) * (3.3 / 1023.0);
  inVoltage33 = analogRead(inputPin3) * (3.3 / 1023.0);
  digitalWrite(outputPin3, LOW);

  //center of pressure
  voltageSum = inVoltage11 + inVoltage12 + inVoltage13 +
               inVoltage21 + inVoltage22 + inVoltage23 +
         inVoltage31 + inVoltage32 + inVoltage33;
         
  voltageX = (inVoltage11*x11) + (inVoltage12*x12) + (inVoltage13*x13) + 
             (inVoltage21*x21) + (inVoltage22*x22) + (inVoltage23*x23) + 
           (inVoltage31*x31) + (inVoltage32*x32) + (inVoltage33*x33);
  copX = voltageX / voltageSum;  //X coordinate
  
  voltageY = (inVoltage11*y11) + (inVoltage12*y12) + (inVoltage13*y13) + 
             (inVoltage21*y21) + (inVoltage22*y22) + (inVoltage23*y23) + 
           (inVoltage31*y31) + (inVoltage32*y32) + (inVoltage33*y33);
  copY = voltageY / voltageSum;  //Y coordinate
  
  // print out COP coordinates
  Serial.print(copX); Serial.print(", "); Serial.println(copY);
   delay(1000);
}

