import { divIcon, type LatLngExpression } from "leaflet"
import { useId, useMemo, useState } from "react"
import { createPortal } from "react-dom"
import { Marker } from "react-leaflet"
import css from "./UserMarker.module.css"


interface UserMarkerProps {
    position: GeolocationPosition
}

export function UserMarker(props: UserMarkerProps) {
    const pos = [props.position.coords.latitude, props.position.coords.longitude] as LatLngExpression;
    const id = useId()
    const icon = useMemo(() => divIcon({
        html: `<div id="${id}"></div>`,
        className: '',
        iconSize: [12, 12],
        iconAnchor: [6, 6],
    }), [id])

    const [markerRendered, setMarkerRendered] = useState(false)

    const target = document.getElementById(id)
    const content = <div className={css.user_marker}></div>

    return <>
        <Marker position={pos} icon={icon}
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