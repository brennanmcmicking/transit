import { useEffect, useState } from "react"
import type { Departure, Route, Stop } from "../../data"
import { DepartureCard } from "./DepartureCard"
import { useClient } from "../../util/client"


interface RouteViewProps {
    stop: Stop
    route: Route
    direction: number
}

export function RouteView(props: RouteViewProps) {
    const [departures, setDepartures] = useState<Departure[]>()
    const client = useClient()

    useEffect(() => {
        client.getDeparturesForRoute(props.route.id, props.stop.id, props.direction)
            .then(setDepartures)
    }, [])

    return <div style={{
        overflow: 'scroll',
        height: '100%',
    }}>
        {departures?.map(d => <DepartureCard departure={d} />)}
    </div>
}