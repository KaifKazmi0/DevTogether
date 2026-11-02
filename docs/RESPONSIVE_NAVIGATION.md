# DevTogether — Responsive Dashboard Navigation

## 1. Feature Overview

We are implementing responsive navigation for the DevTogether dashboard.

The objective is to make the dashboard usable on desktop, tablet, and mobile devices.

### Expected behaviour

**Desktop**
- Sidebar remains visible on the left.
- Navbar stays at the top.
- Main content occupies the remaining space.

**Mobile**
- Sidebar is hidden initially.
- A menu button appears in the Navbar.
- Clicking the menu button opens the Sidebar.
- Sidebar slides in from the left.
- A dark backdrop appears behind the Sidebar.
- Clicking the backdrop closes the Sidebar.
- Selecting a navigation link closes the Sidebar.

---

## 2. Existing Component Architecture

Our dashboard currently uses this component structure:

```text
DashboardLayout
│
├── Sidebar
│
└── Dashboard Main
    │
    ├── Navbar
    │
    └── Main Content
        └── Outlet
```

### Component responsibilities

| Component | Responsibility |
|---|---|
| DashboardLayout | Controls the dashboard layout and shared sidebar state |
| Sidebar | Displays dashboard navigation links |
| Navbar | Displays search, notifications, profile and mobile menu |
| Outlet | Renders the currently selected route |

We preserved the existing component structure instead of rebuilding the dashboard.

---

## 3. React Concept: State Management

We introduced React's `useState` hook in `DashboardLayout.jsx`.

```jsx
const [isSidebarOpen, setIsSidebarOpen] = useState(false);
```

### Understanding the code

- `isSidebarOpen` stores the current sidebar state.
- `setIsSidebarOpen` updates the state.
- `false` is the initial value, meaning the sidebar starts closed.

To open the sidebar:

```jsx
setIsSidebarOpen(true);
```

To close the sidebar:

```jsx
setIsSidebarOpen(false);
```

### What happens when state changes?

1. We call the state setter.
2. React schedules a state update.
3. React renders the component again using the updated state.
4. React reconciles the new UI with the previous UI.
5. React updates the necessary parts of the DOM.

**Important:** We should not directly reassign a state variable. React state should be updated using its setter function.

---

## 4. React Concept: Lifting State Up

Both Navbar and Sidebar need to interact with the same sidebar state.

Instead of maintaining separate state in both components, we placed the state inside their closest common parent: `DashboardLayout`.

This technique is called **lifting state up**.

### Why?

If Navbar and Sidebar each maintained their own state, they would not automatically stay synchronized.

By keeping the state in DashboardLayout, we establish one source of truth.

```text
DashboardLayout
│
│  Owns isSidebarOpen
│
├── Navbar
│     │
│     └── Requests sidebar to open
│
└── Sidebar
      │
      └── Requests sidebar to close
```

### Key principle

Keep shared state in the closest common parent of the components that need it.

Not every state needs to live in the parent. If only one component needs a particular state, that component can usually own it.

---

## 5. React Concept: Props

Props allow parent components to pass data and functions to child components.

We use props to connect Navbar and Sidebar to the state owned by DashboardLayout.

### Navbar props

```jsx
<Navbar
  onMenuClick={openSidebar}
  isSidebarOpen={isSidebarOpen}
/>
```

Navbar receives:

```jsx
function Navbar({ onMenuClick, isSidebarOpen }) {
```

- `onMenuClick` is a callback function.
- `isSidebarOpen` tells Navbar whether the sidebar is open.

### Sidebar props

```jsx
<Sidebar
  isOpen={isSidebarOpen}
  onClose={closeSidebar}
/>
```

Sidebar receives:

```jsx
function Sidebar({ isOpen, onClose }) {
```

- `isOpen` controls the sidebar's open CSS class.
- `onClose` requests that the parent close the sidebar.

---

## 6. React Concept: Callback Functions

A callback is a function passed to another component so that the receiving component can call it when something happens.

In DashboardLayout:

```jsx
function openSidebar() {
  setIsSidebarOpen(true);
}

function closeSidebar() {
  setIsSidebarOpen(false);
}
```

Navbar calls `openSidebar` when the menu button is clicked.

Sidebar calls `closeSidebar` after navigation.

This allows child components to communicate user actions to their parent without directly modifying the parent's state.

---

## 7. React Concept: Event Handling

React provides event handlers such as `onClick`.

Example:

```jsx
<button onClick={onMenuClick}>
  Open Menu
</button>
```

When the user clicks the button, React calls the supplied function.

We use click handlers for:

- Opening the sidebar.
- Closing the sidebar through the backdrop.
- Closing the sidebar after selecting a navigation link.

We pass a function reference instead of executing the function immediately during rendering.

---

## 8. Conditional Rendering and CSS Classes

We added a conditional backdrop:

```jsx
{isSidebarOpen && (
  <div
    className="sidebar-backdrop"
    onClick={closeSidebar}
  />
)}
```

### Understanding `&&`

In JavaScript, the logical AND operator can be used for conditional rendering.

- If `isSidebarOpen` is `true`, React renders the backdrop.
- If `isSidebarOpen` is `false`, React does not render it.

We also use a conditional CSS class:

```jsx
<aside className={`sidebar ${isOpen ? "sidebar-open" : ""}`}>
```

When the sidebar is open, the `sidebar-open` class is applied.

CSS uses this class to move the sidebar onto the screen.

---

## 9. Responsive CSS Implementation

Our existing desktop layout uses CSS Grid:

```css
.dashboard-layout {
  display: grid;
  grid-template-columns: 250px minmax(0, 1fr);
  min-height: 100vh;
}
```

This creates:
- A 250px sidebar.
- A flexible main content area.

For mobile, we use a media query:

```css
@media (max-width: 768px) {
  /* Mobile-specific styles */
}
```

### Important CSS concepts

**1. Position fixed**

```css
position: fixed;
```

The sidebar is positioned relative to the viewport rather than occupying a normal grid column.

**2. Transform**

Initially:

```css
transform: translateX(-100%);
```

This moves the sidebar completely outside the left edge of the screen.

When opened:

```css
transform: translateX(0);
```

The sidebar returns to its visible position.

**3. CSS transition**

```css
transition: transform 0.3s ease;
```

This animates the sidebar movement instead of making it appear instantly.

**4. Z-index**

```css
z-index: 1000;
```

We use stacking order to control which elements appear above others.

Our sidebar has a higher `z-index` than the backdrop so it remains clickable.

**5. Inset**

```css
inset: 0;
```

This sets all four positional edges to zero, making the backdrop cover the viewport.

### Mobile CSS behaviour

```css
.sidebar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  width: 250px;
  z-index: 1001;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.sidebar.sidebar-open {
  transform: translateX(0);
}
```

The sidebar is hidden using a transform rather than `display: none`.

This is important because transforms can be animated.

---

## 10. Accessibility

We added accessibility attributes to the mobile menu button.

```jsx
<button
  className="mobile-menu-button"
  onClick={onMenuClick}
  aria-expanded={isSidebarOpen}
  aria-label={
    isSidebarOpen
      ? "Close navigation menu"
      : "Open navigation menu"
  }
>
  <Menu size={22} />
</button>
```

### Understanding the attributes

**aria-label**

Provides an accessible description of the button for assistive technologies.

**aria-expanded**

Communicates whether the controlled navigation menu is currently expanded.

It reflects the actual React state.

Accessibility improvements still worth considering:
- Keyboard Escape to close the sidebar.
- Managing keyboard focus when the sidebar opens.
- Returning focus to the menu button when it closes.

---

## 11. Implementation Progress

| Task | Status |
|---|---|
| Inspect existing components | Completed |
| Understand shared state ownership | Completed |
| Add sidebar state to DashboardLayout | Implemented |
| Pass props to Navbar | Implemented |
| Pass props to Sidebar | Implemented |
| Add mobile menu button | Implemented |
| Add conditional backdrop | Implemented |
| Close sidebar after navigation | Implemented |
| Add responsive CSS | Implemented |
| Add sidebar slide animation | Implemented |
| Add accessible menu attributes | Implemented |
| Test desktop layout | Pending |
| Test mobile layout | Pending |
| Test navigation and backdrop | Pending |
| Review and update documentation | In progress |
| Commit and push changes | Pending |
| Create Pull Request | Pending |

---

## 12. Testing Checklist

After implementation, verify:

| Test | Expected result |
|---|---|
| Open dashboard on desktop | Sidebar visible |
| Open dashboard on mobile | Sidebar hidden |
| Click menu button | Sidebar slides in |
| Click backdrop | Sidebar closes |
| Click navigation link | Sidebar closes |
| Resize mobile to desktop | Desktop layout restored |
| Check accessibility label | Correct menu description |
| Check expanded state | Matches sidebar visibility |

---

## 13. Git Workflow

Current feature branch:

```text
feature/responsive-dashboard-layout
```

After testing:

```bash
git status
git diff
```

Stage only the intended files:

```bash
git add frontend/src/components/layout/DashboardLayout.jsx
git add frontend/src/components/Navbar.jsx
git add frontend/src/components/Sidebar.jsx
git add frontend/src/index.css
git add docs/RESPONSIVE_NAVIGATION.md
```

Review staged changes:

```bash
git diff --cached
```

Commit:

```bash
git commit -m "feat(frontend): add responsive dashboard navigation"
```

Push:

```bash
git push -u origin feature/responsive-dashboard-layout
```

Create a Pull Request targeting `develop` and ask the other collaborator to review it.

---

## 14. Learning Objectives

By completing this feature, we practise:

- React state management using `useState`.
- Lifting state up.
- Props and callback functions.
- Parent-child component communication.
- React event handling.
- Conditional rendering.
- Conditional CSS classes.
- CSS Grid.
- Responsive design and media queries.
- CSS positioning and stacking contexts.
- CSS transitions and transforms.
- Accessibility attributes.
- Git feature branches and Pull Requests.

**The goal is not just to make navigation responsive. It is to understand how React components communicate and how CSS adapts an interface to different screen sizes.**
