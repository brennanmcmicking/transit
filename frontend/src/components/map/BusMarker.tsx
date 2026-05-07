import { useId, useMemo, useState } from "react"
import { createPortal } from "react-dom"
import { Marker } from "react-map-gl/maplibre"
import css from "./BusMarker.module.css"


interface BusMarkerProps {
    latitude: number
    longitude: number
    route: string
}

export function BusMarker(props: BusMarkerProps) {
    const id = useId()
    // const icon = useMemo(() => divIcon({
    //     html: `<div id="${id}"></div>`,
    //     className: '',
    //     iconSize: [32, 32],
    //     iconAnchor: [0, 8],
    // }), [id])

    // const [markerRendered, setMarkerRendered] = useState(false)

    // const target = document.getElementById(id)
    // const content = <div className={css.bus_marker}>{props.route}</div>

    return <>
        <Marker
            latitude={props.latitude}
            longitude={props.longitude}
        // icon={icon}
        // eventHandlers={{
        //     add: () => setMarkerRendered(true),
        //     remove: () => setMarkerRendered(false),
        // }}
        >
            <div className={css.bus_marker}>{props.route}</div>
        </Marker>
        {/* {markerRendered && target && createPortal(
            content,
            target
        )} */}
    </>
}