import { divIcon, type LatLngExpression } from "leaflet"
import { useId, useMemo, useState } from "react"
import { createPortal } from "react-dom"
import { Marker } from "react-leaflet"
import css from "./BusMarker.module.css"


interface BusMarkerProps {
    position: LatLngExpression
    route: string
}

export function BusMarker(props: BusMarkerProps) {
    const id = useId()
    const icon = useMemo(() => divIcon({
        html: `<div id="${id}"></div>`, // Placeholder for our React component
        className: '', // Remove default Leaflet icon styling
        iconSize: [32, 32], // Adjust size as needed
        iconAnchor: [0, 0], // Adjust anchor point
    }), [id])

    const [markerRendered, setMarkerRendered] = useState(false)

    const target = document.getElementById(id)
    const content = <div className={css.bus_marker}>{props.route}</div>

    return <>
        <Marker position={props.position} icon={icon}
            eventHandlers={{
                add: () => setMarkerRendered(true),
                remove: () => setMarkerRendered(false),
            }}
        />
        {markerRendered && target && createPortal(
            content,
            target
        )}
    </>
}