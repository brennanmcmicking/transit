import { HourglassIcon, LandPlotIcon } from "lucide-react"
import { type ProviderType } from "../providers/ControlPaneProvider"
import { FloatingButton } from "./FloatingButton"

interface SortButtonProps {
    sortStateHook: ProviderType["sortStateHook"]
}

export function SortButton(props: SortButtonProps) {
    const sort = props.sortStateHook[0]
    const setSort = props.sortStateHook[1]

    return <FloatingButton
        top='-40px'
        right='8px'
        left="auto"
        onClick={() => {
            switch (sort) {
                case "DISTANCE": setSort("TIME"); break
                case "TIME": setSort("DISTANCE"); break
                default: setSort("TIME"); break
            }
        }}
    >
        {sort === "DISTANCE" && <HourglassIcon width="32px" height="32px" />}
        {sort === "TIME" && <LandPlotIcon width="32px" height="32px" />}
    </FloatingButton>
}