import { Button, FormControl, IconButton, MenuItem, Paper, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TablePagination, TableRow, TextField } from "@mui/material";
import type { Loan as LoanModel } from "../../types/Loan";
import styles from "./Loan.module.css";
import { useState } from "react";
import { ClearIcon, DatePicker } from "@mui/x-date-pickers";
import {
    useGetLoansQuery,
    useCreateLoanMutation,
    useDeleteLoanMutation,
    useGetClientsQuery,
    useGetGamesQuery,

} from "../../redux/services/ludotecaApi";
import { useAppDispatch } from "../../redux/hooks";
import { LoaderContext } from "../../context/LoaderProvider";
import { useContext, useEffect } from "react";
import { setMessage } from "../../redux/features/messageSlice";
import CreateLoan from "./components/CreateLoan";
import { ConfirmDialog } from "../../components/ConfirmDialog";
import type { BackError } from "../../types/appTypes";
import type { Client } from "../../types/Client";
import type { Game } from "../../types/Game";
import moment from "moment";

export const Loan = () => {
    const [loans, setLoans] = useState<LoanModel[]>([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(5);
    const [total, setTotal] = useState(0);
    const [openCreate, setOpenCreate] = useState(false);
    const [idToDelete, setIdToDelete] = useState("");
    const [filterTitle, setFilterTitle] = useState("");
    const [filterClient, setFilterClient] = useState("");
    const [filterDate, setFilterDate] = useState<Date | null>(null);

    const filters = {
        idGame: filterTitle ? Number(filterTitle) : undefined,
        idClient: filterClient ? Number(filterClient) : undefined,
        date: filterDate ? moment(filterDate).format("YYYY-MM-DD") : undefined,
        pageNumber,
        pageSize,
    };

    const { data, error, isLoading } = useGetLoansQuery(filters);
    const { data: clients } = useGetClientsQuery(null);
    const { data: games } = useGetGamesQuery({});

    const dispatch = useAppDispatch();
    const loader = useContext(LoaderContext);

    const handleChangePage = (
        _event: React.MouseEvent<HTMLButtonElement> | null,
        newPage: number
    ) => {
        setPageNumber(newPage);
    };

    const handleChangeRowsPerPage = (
        event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        setPageNumber(0);
        setPageSize(parseInt(event.target.value, 10));
    };

    const [createLoanApi, { isLoading: isLoadingCreate }] =
        useCreateLoanMutation();

    const [deleteLoanApi, { isLoading: isLoadingDelete, error: errorDelete }] =
        useDeleteLoanMutation();
    console.log({ idGame: filterTitle, idClient: filterClient, date: filterDate, pageNumber, pageSize });



    const createLoan = (loan: LoanModel) => {
        setOpenCreate(false);

        createLoanApi(loan)
            .unwrap()
            .then(() => {
                dispatch(
                    setMessage({ text: "Préstamo creado correctamente", type: "ok" })
                );
            })
            .catch(() => {
                dispatch(
                    setMessage({
                        text: "Se ha producido un error al crear el préstamo",
                        type: "error",
                    })
                );
            });

    };

    const deleteLoan = () => {
        deleteLoanApi(idToDelete)
            .then(() => {
                dispatch(
                    setMessage({ text: "Préstamo eliminado correctamente", type: "ok" })
                );
                setIdToDelete("");
            })
            .catch((err) => console.log(err));
    };


    useEffect(() => {
        if (error) {
            dispatch(setMessage({ text: "Se ha producido un error", type: "error" }));
        }
    }, [dispatch, error]);


    useEffect(() => {
        loader.showLoading(
            isLoadingCreate || isLoading || isLoadingDelete
        );
    }, [isLoadingCreate, isLoading, isLoadingDelete, loader]);

    useEffect(() => {
        if (data && data.content) {
            setLoans(data.content);
            setTotal(data.totalElements);
            console.log("Loans data:", data.content);
        } else {
            setLoans([]);
            setTotal(0);
            console.log("No loans data available");
        }
    }, [data]);

    useEffect(() => {
        if (errorDelete) {
            if ("status" in errorDelete) {
                dispatch(
                    setMessage({
                        text: (errorDelete?.data as BackError).msg,
                        type: "error",
                    })
                );
            }
        }
    }, [errorDelete, dispatch]);



    return (
        <div className="container">
            <h1>Préstamos</h1>
            <div className={styles.filter}>
                <FormControl variant="standard" sx={{ m: 1, minWidth: 220 }}>
                    <TextField
                        margin="dense"
                        select
                        id="title"
                        label="Titulo"
                        fullWidth
                        value={filterTitle}
                        variant="standard"
                        onChange={(event) => {
                            setFilterTitle(event.target.value);
                            setPageNumber(0);
                        }}

                    >                            
                    <MenuItem value="">Selecciona un juego</MenuItem>
                        {games &&
                            games.map((option: Game) => (
                                <MenuItem key={option.id} value={option.id}>
                                    {option.title}
                                </MenuItem>
                            ))}
                    </TextField>
                </FormControl>
                <FormControl variant="standard" sx={{ m: 1, minWidth: 220 }}>
                    <TextField
                        margin="dense"
                        id="client"
                        select
                        label="Cliente"
                        defaultValue="''"
                        fullWidth
                        variant="standard"
                        name="client"
                        value={filterClient}
                        onChange={(event) => {
                            setFilterClient(event.target.value);
                            setPageNumber(0);
                        }}
                    >
                        <MenuItem value="">Selecciona un cliente</MenuItem>
                        {clients &&
                            clients.map((option: Client) => (
                                <MenuItem key={option.id} value={option.id}>
                                    {option.name}
                                </MenuItem>
                            ))}
                    </TextField>
                </FormControl>
                <FormControl variant="standard" sx={{ m: 1, minWidth: 220 }}>
                    <DatePicker
                        label="Fecha"
                        format="dd/MM/yyyy"
                        value={filterDate}
                        onChange={(date) => {
                            setFilterDate(date);
                            setPageNumber(0);
                        }}
                    />
                </FormControl>
                <Button
                    variant="outlined"
                    onClick={() => {
                        setFilterClient("");
                        setFilterTitle("");
                        setFilterDate(null);
                    }}
                >
                    Limpiar
                </Button>
            </div>
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 500 }} aria-label="custom pagination table">
                    <TableHead sx={{
                        "& th": {
                            backgroundColor: "lightgrey",
                        },
                    }}>
                        <TableRow>
                            <TableCell>Identificador</TableCell>
                            <TableCell>Nombre del juego</TableCell>
                            <TableCell>Nombre del cliente</TableCell>
                            <TableCell>Fecha de préstamo</TableCell>
                            <TableCell>Fecha de devolución</TableCell>
                            <TableCell align="right"></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {loans.map((loan: LoanModel) => (
                            <TableRow key={loan.id}>
                                <TableCell component="th" scope="row">
                                    {loan.id}
                                </TableCell>
                                <TableCell>{loan.game?.title}</TableCell>
                                <TableCell>{loan.client?.name}</TableCell>
                                <TableCell>{new Date(loan.startDate).toLocaleDateString("es-ES")}</TableCell>
                                <TableCell>{new Date(loan.endDate).toLocaleDateString("es-ES")}</TableCell>
                                <TableCell align="right">
                                    <div className={styles.tableActions}>
                                        <IconButton
                                            color="error"
                                            aria-label="delete"

                                            onClick={() => {
                                                setIdToDelete(loan.id);
                                            }}
                                        > <ClearIcon /></IconButton>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                    <TableFooter>
                        <TableRow>
                            <TablePagination
                                rowsPerPageOptions={[5, 10, 25]}
                                colSpan={6}
                                count={total}
                                rowsPerPage={pageSize}
                                page={pageNumber}
                                SelectProps={{
                                    inputProps: {
                                        "aria-label": "rows per page",
                                    },
                                    native: true,
                                }}
                                onPageChange={handleChangePage}
                                onRowsPerPageChange={handleChangeRowsPerPage}
                            />
                        </TableRow>
                    </TableFooter>
                </Table>
            </TableContainer>
            <div className="newButton">
                <Button variant="contained" onClick={() => setOpenCreate(true)}>Nuevo préstamo</Button>

                {openCreate && (
                    <CreateLoan
                        create={createLoan}
                        closeModal={() => {
                            setOpenCreate(false);
                        }}
                    />
                )}
                {!!idToDelete && (
                    <ConfirmDialog
                        title="Eliminar Préstamo"
                        text="Atención si borra el préstamo se perderán sus datos. ¿Desea eliminar el préstamo?"
                        confirm={deleteLoan}
                        closeModal={() => setIdToDelete("")}
                    />
                )}
            </div>
        </div>
    );
};