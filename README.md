# interview_android
This is the code base for the Android interviews we conduct at Carda Health.

# Services
There are two Services provided by a precompiled library that will be used during our interview process. Below is the documentation available for each Service.

## SensorService
Provides discovery, connection, and data streaming for CardaHealth biometric sensors.

### Typical usage
```kotlin
val service = SensorStateService()

// 1. Discover available sensors
val sensorIds = service.discover()

// 2. Connect to a sensor and observe connection progress
service.connect(sensorIds.first()).collect { state ->
  
}
```

### discover() method
Discovers any available sensors.
Returns a list of available sensor identifiers.

### connect(id: String) method
Connects to a sensor and returns a flow representing its connection state.
The returned flow emits an integer state that progresses through three stages:
- `0` — disconnected
- `1` — connecting
- `2` — connected

Throws IllegalArgumentException if no sensor with the given id is connectable.
Throws IllegalStateException if a connection error occurs during a state transition.

### getDataFlow(id: String) method
Retrieves the live data flow for a connected sensor.
Must only be called after connect() has emitted state `2` (connected). Each emission is a ByteBuffer rewound to position 0, with one byte per reading in the following layout:
| Byte | Meaning                        | Range   | Always present |
|------|--------------------------------|---------|----------------|
| 0    | Battery level (%)              | 0–100   | Yes            |
| 1    | Heart rate (BPM)               | 0–250   | Yes            |
| 2    | Blood oxygen saturation / SpO2 | 0–100   | No             |

Throws IllegalArgumentException if no sensor with the given id is currently connected.
Throws IllegalStateException if the sensor reports a data stream error mid-collection.

## APIService
Provides API endpoints to receive information about sensors available to the user, and to report different sensor values.

### reportHR(data: String) method
Expects to receive a serialized JSON with the format
```json
{
  "value": Int
}
```

### reportBatchHR(data: String) method
Expects to receive a serialized JSON with the format
```json
{
  "values": [Int]
}
```

### getAssignedSensors() method
Responds with a JSON String with the format
```json
{
  "id": String,
  "brand": String,
  "model": String,
  "assignment_date": Unix Epoch,
  "capabilities": String[] // Options: "heart_rate", "spo2"
}
```
