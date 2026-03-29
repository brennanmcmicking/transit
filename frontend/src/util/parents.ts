

export function getParents(element: HTMLElement) {
    const parents = []
    let current = element;
    while (current.parentElement !== null) {
        current = current.parentElement
        parents.push(current)
    }
    return parents
}