import type { Departure, Stop } from "../../data"
import { DepartureCard } from "./DepartureCard"


interface StopViewProps {
    stop: Stop
    departures: Departure[]
}

export function StopView(props: StopViewProps) {



    return <div style={{
        overflow: 'scroll',
        height: '100%',
    }}>
        {props.departures.map(d => <DepartureCard departure={d} />)}
    </div>
}