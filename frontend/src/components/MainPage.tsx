import { useCallback, useEffect, useState } from 'react'
import { Map } from './map/Map'
import { NearbyView } from './info/NearbyView'
import { type Stop, type Bus } from '../data'
import { useClient } from '../util/client'
import { Flex } from './containers/Flex'
import { useControlPane } from '../providers/ControlPaneProvider'
import css from './MainPage.module.css'
import { StopView } from './info/StopView'
import { RouteView } from './info/RouteView'
import { BackButton } from './BackButton'
import { SortButton } from './SortButton'


interface MainPageProps {
    location: GeolocationPosition
}

export function MainPage(props: MainPageProps) {
    const client = useClient()

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { viewStateHook, sortStateHook } = useControlPane()
    const [viewState] = viewStateHook

    const [busses, setBusses] = useState<Bus[]>()
    const [stops, setStops] = useState<Stop[]>()

    const updateBusses = useCallback(() => {
        client.getBusses()
            .then(setBusses)
    }, [client])

    const updateStops = useCallback(() => {
        client.getStops()
            .then(setStops)
    }, [client])

    useEffect(() => {
        updateBusses()
        updateStops()

        const intervalId = setInterval(updateBusses, 60 * 1000);
        return () => clearInterval(intervalId);
    }, [updateBusses, updateStops])

    return (
        <div style={{ width: '100vw', height: '100vh' }}>
            {location && <Flex className={css.container} direction='column'>
                <Map location={props.location} busses={busses ?? []} stops={stops ?? []} />
                <div style={{ maxHeight: '50%', position: 'relative' }} className={css.info_box_container}>
                    <div id="refresh-indicator" className={css.refresh_indicator}></div>
                    {viewState.type === 'nearby-departures' && <NearbyView location={props.location} />}
                    {viewState.type === 'stop' && <>
                        <StopView stop={viewState.stop} departures={viewState.departures} />
                        <BackButton location={props.location} />
                    </>}
                    {viewState.type === 'departure' && <>
                        <RouteView
                            stop={viewState.departure.stop}
                            route={viewState.departure.route}
                            direction={viewState.departure.direction}
                            latitude={props.location.coords.latitude}
                            longitude={props.location.coords.longitude}
                        />
                        <BackButton location={props.location} />
                    </>
                    }
                    {viewState.type === 'nearby-departures' && <SortButton sortStateHook={sortStateHook} />}
                </div>
            </Flex>}
        </div>
    )
}