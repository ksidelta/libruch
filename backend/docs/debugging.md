JpaEventStorageEngine - stores event
TrackingEventProcessor - processes event

in runBlocking - TrackingEventProcessor won't find event
in runBlocking(Dispatchers.IO) - TrackingEventProcessor will find event

[ ] Does JpaEventStorageEngine succeeds in saving event?