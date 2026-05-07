import { useCallback, useEffect, useRef, useState } from "react";
import type { DepartureAndDistance, SortBy } from "../../data";
import { useClient } from "../../util/client";
import { DepartureCard } from "./DepartureCard";
import { Ellipsis } from "lucide-react";
import { Flex } from "../containers/Flex";
import { useControlPane } from "../../providers/ControlPaneProvider";

interface NearbyViewProps {
    location: GeolocationPosition
}

export function NearbyView(props: NearbyViewProps) {
    const [nearby, setNearby] = useState<DepartureAndDistance[]>()
    const client = useClient()
    const { sortStateHook } = useControlPane()
    const sortBy = sortStateHook[0]

    const updateNearby = useCallback((latitude: number, longitude: number, sortBy: SortBy) => client.getNearbyDepartures(
        latitude, longitude, 2, sortBy).then(setNearby)
        , [client])

    const refreshIndicator = useRef(document.getElementById('refresh-indicator') ?? undefined)

    useEffect(() => {
        updateNearby(props.location.coords.latitude, props.location.coords.longitude, sortBy)

        if (refreshIndicator.current) {
            // refreshIndicator.clientWidth = '100%'
            refreshIndicator.current.style.width = '100%'
        }

        let counter = 0

        const intervalId = setInterval(() => {
            if (counter >= 30) {
                updateNearby(props.location.coords.latitude, props.location.coords.longitude, sortBy)
                counter = 0
            }
            if (refreshIndicator.current) {
                refreshIndicator.current.style.width = `${(30 - counter) / 30 * 100}%`
            }
            counter += 1
        }, 1000)
        return () => clearInterval(intervalId)
    }, [props.location.coords.latitude, props.location.coords.longitude, sortBy, updateNearby]);

    return <div style={{
        display: 'flex',
        flexDirection: 'column',
        width: '100%',
        pointerEvents: 'all',
        overflow: 'scroll',
        height: '100%',
    }}>
        {nearby?.map(departure => <DepartureCard departure={departure.departure} distanceKm={departure.distance} />)}
        {!nearby && <Flex align="center" justify="center"><Ellipsis /></Flex>}
    </div>
}