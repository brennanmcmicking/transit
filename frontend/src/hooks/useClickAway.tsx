import { useEffect } from "react";
import { getParents } from "../util/parents";


export function useClickAway(id: string, handler: () => void) {

    useEffect(() => {
        const clickHandler = (event: PointerEvent) => {
            const target = (event.target as HTMLElement)
            const ids = [target, ...getParents(target)].map(elem => elem.id)
            if (!ids.includes(id)) {
                handler()
            }
        }

        document.addEventListener('click', clickHandler)
        return () => document.removeEventListener('click', clickHandler)
    }, [])

}