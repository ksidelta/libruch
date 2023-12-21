import { AuthorizedLayout } from "../components/Layout";
import {
  EventListenerContext,
  EventListenerProvider,
  // useEventListener,
} from "../features/EventsSteam/EventListener";

const Organisation = () => {
  const streamURL = "/api/organisation/stream";
  // const eventListenerContext = useEventListener();

  return (
    <AuthorizedLayout>
      <EventListenerProvider url={streamURL}>
        <EventListenerContext.Consumer>
          {(organizationsContext) => (
            <>
              Organizancje są takie o:
              <pre>{JSON.stringify(organizationsContext)}</pre>
            </>
          )}
        </EventListenerContext.Consumer>
      </EventListenerProvider>
    </AuthorizedLayout>
  );
};

export default Organisation;
