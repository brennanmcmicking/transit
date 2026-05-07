import css from "./UserMarker.module.css"
import { Marker } from "react-map-gl/maplibre"


interface UserMarkerProps {
    latitude: number
    longitude: number
}

export function UserMarker(props: UserMarkerProps) {

    return <>
        <Marker
            latitude={props.latitude}
            longitude={props.longitude}
        >
            <div className={css.user_marker} />
        </Marker>
    </>
}