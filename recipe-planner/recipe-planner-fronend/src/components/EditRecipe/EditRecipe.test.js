import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import EditRecipe from './EditRecipe';
import {getRecipe, updateRecipe} from '../../apis/recipeApi';

jest.mock('../../apis/recipeApi');

const RECIPE_ID = '11111111-1111-1111-1111-111111111111';

const lasagne = {
    id: RECIPE_ID,
    name: 'Lasagne',
    description: 'Classic',
    imageUrl: 'https://img/lasagne.jpg',
    ingredients: [
        {id: 'a', name: 'Tomato', comment: 'The big ones', unit: 'PIECE', amount: 5},
        {id: 'b', name: 'Pasta', comment: null, unit: 'GRAMM', amount: 250},
    ],
};

const renderPage = () => render(
    <MemoryRouter initialEntries={[`/recipes/${RECIPE_ID}/edit`]}>
        <Routes>
            <Route path="/recipes/:recipeId/edit" element={<EditRecipe/>}/>
            <Route path="/" element={<div>Browse page</div>}/>
        </Routes>
    </MemoryRouter>
);

const type = (element, value) => fireEvent.change(element, {target: {value}});

describe('EditRecipe', () => {
    beforeEach(() => jest.resetAllMocks());

    test('loads the recipe and fills the form with the existing data', async () => {
        getRecipe.mockResolvedValue(lasagne);
        renderPage();

        expect(await screen.findByDisplayValue('Lasagne')).toBeInTheDocument();
        expect(getRecipe).toHaveBeenCalledWith(RECIPE_ID);
        expect(screen.getByText('Edit Recipe')).toBeInTheDocument();
        expect(screen.getByDisplayValue('Classic')).toBeInTheDocument();
        expect(screen.getByDisplayValue('Tomato')).toBeInTheDocument();
        expect(screen.getByDisplayValue('250')).toBeInTheDocument();
    });

    test('saves the changes with PUT and keeps the ids of existing ingredients', async () => {
        getRecipe.mockResolvedValue(lasagne);
        updateRecipe.mockResolvedValue({});
        renderPage();

        type(await screen.findByDisplayValue('Lasagne'), 'Veggie Lasagne');
        type(screen.getByDisplayValue('250'), '300');
        fireEvent.click(screen.getAllByRole('button', {name: 'Remove ingredient'})[0]);
        fireEvent.click(screen.getByRole('button', {name: 'Submit'}));

        await waitFor(() => expect(screen.getByText('Browse page')).toBeInTheDocument());
        expect(updateRecipe).toHaveBeenCalledWith(RECIPE_ID, {
            name: 'Veggie Lasagne',
            description: 'Classic',
            imageUrl: 'https://img/lasagne.jpg',
            ingredients: [{id: 'b', name: 'Pasta', unit: 'GRAMM', amount: 300}],
        });
    });

    test('keeps the comment of an ingredient that is not edited in the form', async () => {
        getRecipe.mockResolvedValue(lasagne);
        updateRecipe.mockResolvedValue({});
        renderPage();

        await screen.findByDisplayValue('Lasagne');
        fireEvent.click(screen.getByRole('button', {name: 'Submit'}));

        await waitFor(() => expect(updateRecipe).toHaveBeenCalled());
        expect(updateRecipe.mock.calls[0][1].ingredients[0]).toEqual(
            {id: 'a', comment: 'The big ones', name: 'Tomato', unit: 'PIECE', amount: 5});
    });

    test('validation errors prevent saving', async () => {
        getRecipe.mockResolvedValue(lasagne);
        renderPage();

        type(await screen.findByDisplayValue('Lasagne'), '');
        fireEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(screen.getByText('Name is required')).toBeInTheDocument();
        expect(updateRecipe).not.toHaveBeenCalled();
    });

    test('shows an error when saving fails and stays on the page', async () => {
        getRecipe.mockResolvedValue(lasagne);
        updateRecipe.mockRejectedValue(new Error('Request failed with status code 404'));
        renderPage();

        await screen.findByDisplayValue('Lasagne');
        fireEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('The recipe could not be saved. Please try again.')).toBeInTheDocument();
        expect(screen.queryByText('Browse page')).not.toBeInTheDocument();
    });

    test('shows a message when the recipe does not exist', async () => {
        getRecipe.mockResolvedValue('');
        renderPage();

        expect(await screen.findByText('Recipe not found.')).toBeInTheDocument();
    });

    test('shows a message when the recipe cannot be loaded', async () => {
        getRecipe.mockRejectedValue(new Error('Network Error'));
        renderPage();

        expect(await screen.findByText('The recipe could not be loaded.')).toBeInTheDocument();
    });
});
