# SIP User Agent and Proxy - README

This repository contains the code for implementing a SIP User Agent and Proxy to realize the following scenario:

```
                       +----+
                       | UA |
                       |    |
                       +----+
                          |
                          |3)INVITE
                          |   carol@chicago.com
   chicago.com        +--------+            V
   +---------+ 2)Store|Location|4)Query +-----+
   |Registrar|=======>| Service|<=======|Proxy|sip.chicago.com
   +---------+        +--------+=======>+-----+
         A                      5)Resp      |
         |                                  |
         |                                  |
  1)REGISTER|                                  |
         |                                  |
      +----+                                |
      | UA |<-------------------------------+
cube2214a|    |                            6)INVITE
      +----+                    carol@cube2214a.chicago.com
       carol
```
In this scenario, registration and location services are implemented in the proxy to simplify development. The call scenario involves a single SIP proxy, supporting internal calls within a domain, and does not rely on DNS for proxy resolution. Additionally, simultaneous registration from two different terminals is not allowed, and registrations have a specified lifetime.

## Implementation Details:
The SIP protocol stack will handle the following SIP messages: REGISTER, INVITE, ACK, and BYE.
Simplified response messages include: 100 Trying, 180 Ringing, 200 OK, 404 Not Found, 408 Request Timeout, 486 Busy Here, and 503 Service Unavailable.
The implementation is in Java, and the provided code includes base classes for both User Agent (UA) and Proxy.
Debug mode is available, displaying detailed message traces.
Usage:
For User Agent (UA):

`java UA <SIP-username> <UA-listening-port> <Proxy-IP> <Proxy-listening-port> <debug(true/false)> <registration-time>`
<SIP-username>: Format - username@domain.
<UA-listening-port>: Listening port for UA.
<Proxy-IP>: IP address of the SIP Proxy.
<Proxy-listening-port>: Listening port of the SIP Proxy.
<debug(true/false)>: Enable or disable debug mode.
<registration-time>: Registration time in seconds.
For Proxy:

`java Proxy <Proxy-listening-port> <loose-routing(true/false)> <debug(true/false)>`
<Proxy-listening-port>: Listening port for the SIP Proxy.
<loose-routing(true/false)>: Enable or disable loose routing.
<debug(true/false)>: Enable or disable debug mode.

## Notes:
Default ports are used if not specified.
Only UDP is used for SIP communications.
SDP payload in INVITE and 200 OK messages for voice session description.
The provided code includes an API for SIP message handling.
