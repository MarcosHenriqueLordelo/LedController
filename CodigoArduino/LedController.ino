#include <SoftwareSerial.h>

SoftwareSerial bluetooth(10,11);

#define led1 12
#define led2 2
#define led3 3
String comand;

void setup() {
  Serial.begin(9600);
  bluetooth.begin(9600);

  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);

}

void loop() {
  comand = "";

  if(bluetooth.available()){
    while(bluetooth.available()){
      char caracter = bluetooth.read();
      comand += caracter;
      delay(10);
    }
    
    if (comand.indexOf("led1") >=0){
      digitalWrite(led1, !digitalRead(led1));
    }
    if (comand.indexOf("led2") >=0){
      digitalWrite(led2, !digitalRead(led2));
    }
    if (comand.indexOf("led3") >=0){
      digitalWrite(led3, !digitalRead(led3));
    }
    
    bluetooth.println("{");
    if(digitalRead(led1)){
      bluetooth.println("l1on");
    }else{
      bluetooth.println("l1of");
    }
    if(digitalRead(led2)){
      bluetooth.println("l2on");
    }else{
      bluetooth.println("l2of");
    }
    if(digitalRead(led3)){
      bluetooth.println("l3on");
    }else{
      bluetooth.println("l3of");
    }
    bluetooth.println("}");
 
  }
}
