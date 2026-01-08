import { useEffect, useState } from "react";
import type { Departure } from "../../data";
import { Button } from "../system/Button";

interface NearbyViewProps {
    location: GeolocationPosition
}

export function NearbyView(props: NearbyViewProps) {
    const [nearby, setNearby] = useState<Departure[]>()

    useEffect(() => {
        fetch(`http://localhost:8080/v1/nearby-departures?latitude=${props.location.coords.latitude}&longitude=${props.location.coords.longitude}&maxDistanceKm=2`)
            .then(response => response.json())
            .then(setNearby)
    }, [props.location]);

    return <div style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '4px',
        width: '100%',
        pointerEvents: 'all',
    }}>
        {nearby?.map(departure =>
            <Button onClick={() => { }}>{departure.routeId} {departure.stopId} {departure.departureTime}</Button>
        )}
    </div>
}