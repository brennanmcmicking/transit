import { useEffect, useState } from 'react'
import './App.css'
import { Map } from './components/map/Map'
import { NearbyView } from './components/info/NearbyView'
import { Box } from './components/system/Box'
import type { Bus } from './data'

function App() {
  const [location, setLocation] = useState<GeolocationPosition>()
  const [busses, setBusses] = useState<Bus[]>()

  useEffect(() => {
    const n = navigator.geolocation.watchPosition((position) => {
      setLocation(position)
    });
    return () => navigator.geolocation.clearWatch(n)
  }, [])

  const updateBusses = () => {
    fetch("http://localhost:8080/v1/busses")
      .then(r => r.json())
      .then(setBusses)
  }

  useEffect(() => {
    updateBusses()

    const intervalId = setInterval(updateBusses, 60 * 1000);
    return () => clearInterval(intervalId);
  }, [])

  return (
    <div style={{ width: '100vw', height: '100vh' }}>
      {location && <>
        <Map location={location} busses={busses ?? []} />
        <div style={{
          position: 'absolute',
          left: 0,
          right: 0,
          bottom: 0,
          zIndex: 999,
          height: "100%",
          display: 'flex',
          alignItems: "end",
          justifyContent: 'stretch',
          padding: '4px',
          pointerEvents: 'none'
        }}>
          <Box style={{ maxHeight: '33%' }}>
            <NearbyView location={location} />
          </Box>
        </div>
      </>}

    </div>
  )
}

export default App
