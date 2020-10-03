#include <Ticker.h>
#include <ArduinoJson.h>


    // PubNub example using ESP8266.
    #include <ESP8266WiFi.h>
    #define PubNub_BASE_CLIENT WiFiClient
    #include <PubNub.h>
    static char ssid[] = "";                                                // WIFI SSID or name
    static char pass[] = "";                                                // WIFI's PASSWORD
    const static char pubkey[]  = "";                                       // pubnub's API Publisher key
    const static char subkey[]  = "";                                       // pubnub's API Subscriber key
    const static char channel[] = "";                                       // Channel name over which communication takes place..

    const static char iot_device_name[] = "";                               // give your device a name to be uniquly identified over channel.
    
    void setup() {
        Serial.begin(9600);
        Serial.println("Attempting to connect...");
        WiFi.begin(ssid, pass);
        if(WiFi.waitForConnectResult() != WL_CONNECTED) { 
            // Connect to WiFi.
            Serial.println("Couldn't connect to WiFi.");
            while(1) delay(100);
        }
        else {
            Serial.print("Connected to SSID: ");
            Serial.println(ssid);
            PubNub.begin(pubkey, subkey); // Start PubNub.
            Serial.println("PubNub is set up.");
        }
        
    }
    void loop() {
        { // Subscribe.
            StaticJsonBuffer<400> jsonBuffer;
            PubSubClient* sclient = PubNub.subscribe(channel); // Subscribe.
            if (0 == sclient) { 
                Serial.println("Error subscribing to channel.");
                delay(1000);
                return;
            }

            // message string instantiation;
            String message;

            // getting the whole messgae data:-
            while (sclient->wait_for_data()) { // Print messages.
              
//                Serial.write(sclient->read());

                  char c = (sclient->read());
                  message = message + c;
                  
            }
            
            //filtering the message:-
            String filtered_message = "";
            int length_message = message.length();
            Serial.println(message[1,length_message - 3]);
            
            for(int i = 1; i<=length_message-3;i++){
              filtered_message = filtered_message + message[i];
            }

            //accessing the data from filtered message:-
            JsonObject& root = jsonBuffer.parseObject(filtered_message);
            Serial.println(filtered_message);
            if(!root.success()) {
                  Serial.println("parseObject() failed");

            }

            // Variables containing data recieved from channel
            const char* iot = root["iot"];
            const char* iot_name = root["iot_name"];
            const char* iot_pin = root["iot_pin"];
            
            Serial.println(iot);
            Serial.println(iot_name);
            
            // initialising the temporary variables to perform comparision operations:-
            String temp_iot = iot;
            String temp_iot_name = iot_name;
            String temp_iot_pin = iot_pin;
            
            // start comparing values to perform actions required:-
            if (temp_iot == "echo"){
              Serial.println("if condition is executed");
              sclient->stop();

              { // Publish.
                
                char msg[] = "{\"iot\":\"echo_reply\",\"iot_name\":\"" ;
              
                // adding iot device name:-              
                strcat(msg, iot_device_name);
              
                char enclose[] = "\"}";
              
                strcat( msg, enclose );


                WiFiClient* client = PubNub.publish(channel, msg); // Publish message.
                if (0 == client) {
                  Serial.println("Error publishing messae.");
                  delay(1000);
                  return;
                }
              
                client->stop();
              
              }

                           
            }else if (temp_iot_name == iot_device_name){

              int pin = temp_iot_pin.toInt();
              Serial.println(pin);

              // to turn on the relay module:----(default is off when volt is high):-
              // set this section according to you if needed.
              if (temp_iot == "on"){
                pinMode(pin, OUTPUT);
                digitalWrite(pin, LOW);

                

              }
              // to turn off the relay module:
              else if (temp_iot == "off"){
                pinMode(pin, OUTPUT);
                digitalWrite(pin, HIGH);
             
              }
              
              
            }
                
            sclient->stop();
            
        }

    }
