import type { LatLngExpression } from "leaflet";
import { MapContainer, TileLayer, GeoJSON, AttributionControl } from "react-leaflet";
import type { Bus, Stop } from "../../data";
import { BusMarker } from "./BusMarker";
import { StopMarker } from "./StopMarker";
import { UserMarker } from "./UserMarker";

interface MapProps {
    location: GeolocationPosition
    busses: Bus[]
    stops: Stop[]
}

export function Map(props: MapProps) {
    const pos = [props.location.coords.latitude, props.location.coords.longitude] as LatLngExpression;

    return <MapContainer
        center={pos}
        zoom={15}
        scrollWheelZoom={true}
        touchZoom={true}
        style={{ width: '100%', height: '60%' }}
        attributionControl={false}
    >
        <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
            // url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
            url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
            subdomains="abcd"
        />
        <AttributionControl position="topright" />
        {/* <GeoJSON data={} style={ } /> */}
        <UserMarker position={props.location} />
        {props.busses.filter(bus => bus.latitude != undefined && bus.longitude !== undefined).map(bus =>
            <BusMarker position={[bus.latitude, bus.longitude]} route={bus.label} key={bus.id} />
        )}
        {props.stops.map(stop =>
            <StopMarker position={[stop.latitude, stop.longitude]} stop={stop} key={stop.id} />
        )}
    </MapContainer>
}