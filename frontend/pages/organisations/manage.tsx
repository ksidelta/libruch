import { Button, TextField, Stack } from "@mui/material";
import { AuthorizedLayout } from "../../components/Layout";
import * as React from "react";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";

export const Manage = () => {
  const organisations = [
    {
      name: "Esports Lounge Gdańsk",
      link: "https://libruch.com/organisations/join/xxx1",
    },
    {
      name: "Hackespace Pomorze",
      link: "https://libruch.com/organisations/join/xxx2",
    },
    {
      name: "Też Hackespace Pomorze ale bardzo dluga nazwa taka ze hej",
      link: "https://libruch.com/organisations/join/xxx2",
    },
  ];

  return (
    <AuthorizedLayout>
      <Stack spacing={4}>
        {organisations.map((org) => (
          <Card>
            <CardContent>
              <Typography gutterBottom variant="h5" component="div">
                {org.name}
              </Typography>
              {org.link}
            </CardContent>
            <CardActions>
              <Button size="small" color="error">
                Zresetuj <link rel="stylesheet" href="" />
              </Button>
            </CardActions>
          </Card>
        ))}
      </Stack>
    </AuthorizedLayout>
  );
};

export default Manage;
