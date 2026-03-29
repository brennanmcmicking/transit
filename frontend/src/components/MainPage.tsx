import { useEffect, useState } from 'react'
import { Map } from './map/Map'
import { NearbyView } from './info/NearbyView'
import { Box } from './system/Box'
import { type Stop, type Bus } from '../data'
import { useClient } from '../util/client'
import { Flex } from './containers/Flex'
import { useControlPane } from '../providers/ControlPaneProvider'
import css from './MainPage.module.css'
import { StopView } from './info/StopView'
import { ArrowLeftSquareIcon, SquareArrowLeftIcon } from 'lucide-react'
import { RouteView } from './info/RouteView'


interface MainPageProps {
    location: GeolocationPosition
}

export function MainPage(props: MainPageProps) {
    const client = useClient()

    const [controlPane, setControlPane] = useControlPane()

    const [busses, setBusses] = useState<Bus[]>()
    const [stops, setStops] = useState<Stop[]>()

    const updateBusses = () => {
        client.getBusses()
            .then(setBusses)
    }

    const updateStops = () => {
        client.getStops()
            .then(setStops)
    }

    useEffect(() => {
        updateBusses()
        updateStops()

        const intervalId = setInterval(updateBusses, 60 * 1000);
        return () => clearInterval(intervalId);
    }, [])

    return (
        <div style={{ width: '100vw', height: '100vh' }}>
            {location && <Flex className={css.container} direction='column'>
                <Map location={props.location} busses={busses ?? []} stops={stops ?? []} />
                <div style={{ maxHeight: '40%', position: 'relative' }}>
                    {controlPane.type === 'nearby-departures' && <NearbyView location={props.location} />}
                    {controlPane.type === 'stop' && <>
                        <StopView stop={controlPane.stop} departures={controlPane.departures} />
                        <BackButton location={props.location} />
                    </>}
                    {controlPane.type === 'departure' && <>
                        <RouteView
                            stop={controlPane.departure.stop}
                            route={controlPane.departure.route}
                            direction={controlPane.departure.direction}
                        />
                        <BackButton location={props.location} />
                    </>
                    }
                </div>
            </Flex>}
        </div>
    )
}

interface BackButtonProps {
    location: GeolocationPosition
}

function BackButton(props: BackButtonProps) {
    const [_, setControlPane] = useControlPane()
    return <div style={{
        position: 'absolute',
        left: '8px',
        top: '-16px',
        width: '32px',
        height: '32px',
        zIndex: '999',
        background: 'forestgreen',
        borderRadius: '6px',
    }}
        onClick={() => setControlPane({
            type: 'nearby-departures',
            latitude: props.location.coords.latitude,
            longitude: props.location.coords.longitude,
        })}
    >
        <SquareArrowLeftIcon width="32px" height="32px" />
    </div>
}