import { Box, Button, TextField } from "@mui/material";
import { AuthorizedLayout } from "../../components/Layout";

export const Add = () => {
  return (
    <AuthorizedLayout>
      <Box
        sx={{
          width: 500,
          maxWidth: '100%',
        }}
      >
        <TextField fullWidth label="Nazwa Organizacji" margin="normal" />
      </Box>
      <Box
        sx={{
          width: 500,
          maxWidth: '100%',
        }}
      >
        <Button type="submit" fullWidth variant="contained">Dodaj</Button>
      </Box>
    </AuthorizedLayout>
  );
}

export default Add;
