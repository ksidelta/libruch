import { Box, List, ListItem, ListItemButton, ListItemText } from "@mui/material";
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
          {(ctx) => (
            <Box
              sx={{ width: "100%", maxWidth: 360, bgcolor: "#eee" }}
            >
              {ctx.data ? (
                <List>
                  {(ctx.data.organisations as Array<any>).map((org) => (
                    <ListItem disablePadding key={org.organisationId}>
                      <ListItemButton>
                        <ListItemText primary={org.organisationName} />
                      </ListItemButton>
                    </ListItem>
                  ))}
                </List>
              ) : (
                <p>Loading</p>
              )}
            </Box>
          )}
        </EventListenerContext.Consumer>
      </EventListenerProvider>
    </AuthorizedLayout>
  );
};

export default Organisation;
