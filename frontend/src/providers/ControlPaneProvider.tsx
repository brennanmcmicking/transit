import { createContext, useContext, useState, type Dispatch, type ReactNode, type SetStateAction } from "react";
import type { Departure, Stop } from "../data";

interface NearbyDeparturesControlPaneState {
    type: "nearby-departures"
    latitude: number
    longitude: number
}

interface StopControlPaneState {
    type: "stop"
    stop: Stop
    departures: Departure[]
}

interface DepartureControlPaneState {
    type: "departure"
    departure: Departure
}

type ControlPaneState = NearbyDeparturesControlPaneState | StopControlPaneState | DepartureControlPaneState

type ProviderType = [ControlPaneState, Dispatch<SetStateAction<ControlPaneState>>]

const ControlPaneContext = createContext<ProviderType | undefined>(undefined)

interface ControlPaneProviderProps {
    children: ReactNode
    location: GeolocationPosition,
}

export function ControlPaneProvider(props: ControlPaneProviderProps) {
    const stateHook = useState<ControlPaneState>({
        type: 'nearby-departures',
        latitude: props.location.coords.latitude,
        longitude: props.location.coords.longitude,
    })
    return <ControlPaneContext.Provider value={stateHook}>
        {props.children}
    </ControlPaneContext.Provider>
}

export function useControlPane() {
    const ctx = useContext(ControlPaneContext)

    if (ctx === undefined) {
        throw new Error("Must use useControlPane within a ControlPaneStateProvider")
    }

    return ctx
}