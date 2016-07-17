#define BAUD_RATE 9600

// Temp variable to receive messages
String tmp; 

// Coordinates
float x, y;
String msg;

void shuffleCoordinates(){
  // Force Blueduino to start sending
  Serial.read();
  // Initialiaze random number generator with a fairly random input, such as analogRead() on an unconnected pin
  randomSeed(analogRead(0));
  x = random(0,2);
  
  randomSeed(analogRead(0));
  y = random(0,2);

  x += float(random(1,4)) / 4.0;
  y += float(random(1,4)) / 4.0;

  msg = String(x) + "," + String(y);
}

void setup() {
  Serial.begin(BAUD_RATE);
  Serial1.begin(BAUD_RATE); 

  shuffleCoordinates();
  
  Serial.println(msg);
}

void loop() {
  // Read input from phone
  while (Serial1.available() > 0)  {
    tmp += char(Serial1.read());
    delay(2);
  }

  while (Serial.available() > 0)  {
    tmp += char(Serial.read());
    delay(2);
  }

  if(tmp.length() > 0) {
    Serial.println(tmp);
    tmp = "";
  }

  shuffleCoordinates();
  
  delay(5000);
  
  char* buf = (char*) malloc(sizeof(char)*msg.length()+1);
  msg.toCharArray(buf, msg.length()+1);

  // Send to phone
  Serial1.write(buf);

  Serial.println(msg + ". Freeing the memory");
  free(buf);
}
