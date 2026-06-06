import { useEffect, useState } from "react"
import type { DepartureAndDistance, Route, Stop } from "../../data"
import { DepartureCard } from "./DepartureCard"
import { useClient } from "../../util/client"


interface RouteViewProps {
    stop: Stop
    route: Route
    direction: number
    latitude: number
    longitude: number
}

export function RouteView(props: RouteViewProps) {
    const [departures, setDepartures] = useState<DepartureAndDistance[]>()
    const client = useClient()

    useEffect(() => {
        client.getDeparturesForRoute(
            props.route.id,
            props.stop.id,
            props.direction,
            props.latitude,
            props.longitude,
        )
            .then(setDepartures)
    }, [props, client])

    return <div style={{
        overflow: 'scroll',
        height: '100%',
    }}>
        {departures?.map(d => <DepartureCard departure={d.departure} distanceKm={d.distance} />)}
    </div>
}