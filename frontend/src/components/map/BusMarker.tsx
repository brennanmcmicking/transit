import { Marker } from "react-map-gl/maplibre"
import css from "./BusMarker.module.css"


interface BusMarkerProps {
    latitude: number
    longitude: number
    route: string
}

export function BusMarker(props: BusMarkerProps) {
    return <>
        <Marker
            latitude={props.latitude}
            longitude={props.longitude}
        >
            <div className={css.bus_marker}>{props.route}</div>
        </Marker>
    </>
}