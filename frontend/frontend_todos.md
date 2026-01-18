# Frontend To-Do List

This list outlines the tasks required to implement the frontend application based on the provided requirements.

## 1. Project Setup
- [x] Initialize React project with Vite and TypeScript.
- [x] Install essential dependencies (e.g., React Router, a state management library, a UI component library).

## 2. Authentication (Login/Logout)
- [x] Develop Login component.
- [x] Develop Register component (if user self-registration is needed).
- [ ] Integrate authentication service to interact with AuthAPI.
- [ ] Implement session management (JWT storage, user context).
- [ ] Create logout functionality.

## 3. Routing
- [x] Configure React Router for different user roles (Guest, Client, Event Owner).
- [ ] Implement protected routes based on authentication status and user roles.

## 4. Guest/Unauthenticated Client Functionality
- [ ] Create a public-facing "Events" page.
- [ ] Fetch public event and event package data from the EventAPI.
- [ ] Display fetched data in an accessible and user-friendly format.

## 5. Client Functionality
- [ ] Develop a "My Profile" page for personal information management.
- [ ] Implement features to update personal information via ClientAPI.
- [ ] Create a "My Tickets" page to display purchased tickets.
- [ ] Fetch purchased ticket information from ClientAPI.
- [ ] Implement "Purchase Tickets" workflow, interacting with EventAPI.
- [ ] Ensure clients can view public event information.

## 6. Event Owner Functionality
- [ ] Design and implement an "Event Management" dashboard/page.
- [ ] Develop features to create new events/packages via EventAPI.
- [ ] Implement functionality to edit and delete owned events/packages via EventAPI.
- [ ] Enable viewing of public information for all events/packages (with management capabilities).
- [ ] Create a "Client List" or "Ticket Holders" page for events owned by the user.
- [ ] Fetch relevant client information for owned events via ClientAPI.

## 7. UI/UX
- [x] Implement consistent theming (e.g., dark mode) and refine existing styles.
- [x] Design and implement consistent UI components (e.g., buttons, input fields, navigation bars).
- [x] Select and integrate a suitable UI component library (e.g., Material-UI, Bootstrap, Ant Design) for streamlined development.
- [ ] Ensure the application is fully responsive across different devices.

## 8. Error Handling and Loading States
- [ ] Implement comprehensive error handling mechanisms for all API interactions.
- [ ] Integrate loading indicators to enhance user experience during data fetching.

## 9. CORS Configuration
- [ ] (Note for backend) Ensure backend services are properly configured for Cross-Origin Resource Sharing (CORS) to allow frontend access.