import { useEffect, useState } from 'react'
import { MainPage } from './components/MainPage'
import { ControlPaneProvider } from './providers/ControlPaneProvider';

function App() {
  const [location, setLocation] = useState<GeolocationPosition>()

  useEffect(() => {
    const n = navigator.geolocation.watchPosition((position) => {
      setLocation(position)
    });
    return () => navigator.geolocation.clearWatch(n)
  }, [])

  return (
    location && <ControlPaneProvider location={location}>
      <MainPage location={location} />
    </ControlPaneProvider>
  )
}

export default App
