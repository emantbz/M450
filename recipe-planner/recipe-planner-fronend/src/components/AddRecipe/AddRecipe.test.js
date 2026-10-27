import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import AddRecipe from './AddRecipe';
import {validate} from '../RecipeForm/RecipeForm';
import {addRecipe} from '../../apis/recipeApi';

jest.mock('../../apis/recipeApi');

const type = (label, value) => fireEvent.change(screen.getByLabelText(label), {target: {value}});
const click = (name) => fireEvent.click(screen.getByRole('button', {name}));

const renderPage = () => render(
    <MemoryRouter initialEntries={['/new-menues']}>
        <Routes>
            <Route path="/new-menues" element={<AddRecipe/>}/>
            <Route path="/" element={<div>Browse page</div>}/>
        </Routes>
    </MemoryRouter>
);

describe('AddRecipe', () => {
    beforeEach(() => jest.resetAllMocks());

    test('sends the recipe with its ingredients to the backend and goes back to the list', async () => {
        addRecipe.mockResolvedValue({});
        renderPage();

        type('Recipe Name:', 'Pancakes');
        type('Description:', 'Sweet');
        type('Image URL:', 'https://img/pancakes.jpg');
        click('Add Ingredient');
        type('Ingredient name', 'Flour');
        type('Unit', 'GRAMM');
        type('Quantity', '500');
        click('Submit');

        await waitFor(() => expect(screen.getByText('Browse page')).toBeInTheDocument());
        expect(addRecipe).toHaveBeenCalledWith({
            name: 'Pancakes',
            description: 'Sweet',
            imageUrl: 'https://img/pancakes.jpg',
            ingredients: [{name: 'Flour', unit: 'GRAMM', amount: 500}],
        });
    });

    test('does not send anything when the name is missing', () => {
        renderPage();

        click('Submit');

        expect(screen.getByText('Name is required')).toBeInTheDocument();
        expect(addRecipe).not.toHaveBeenCalled();
    });

    test('a removed ingredient is not sent', async () => {
        addRecipe.mockResolvedValue({});
        renderPage();

        type('Recipe Name:', 'Water');
        click('Add Ingredient');
        click('Remove ingredient');
        click('Submit');

        await waitFor(() => expect(addRecipe).toHaveBeenCalled());
        expect(addRecipe.mock.calls[0][0].ingredients).toEqual([]);
    });

    test('shows an error message when the backend fails', async () => {
        addRecipe.mockRejectedValue(new Error('Network Error'));
        renderPage();

        type('Recipe Name:', 'Pancakes');
        click('Submit');

        expect(await screen.findByText('The recipe could not be saved. Please try again.')).toBeInTheDocument();
        expect(screen.queryByText('Browse page')).not.toBeInTheDocument();
    });
});

describe('validate', () => {
    const recipe = {name: 'Pancakes', description: '', imageUrl: ''};

    test('accepts a valid recipe', () => {
        expect(validate(recipe, [{key: 1, name: 'Flour', unit: 'GRAMM', amount: '500'}]))
            .toEqual({ingredients: {}});
    });

    test('rejects an empty name and ingredients without name or with an invalid quantity', () => {
        const errors = validate({...recipe, name: '  '}, [
            {key: 1, name: '', unit: 'PIECE', amount: '1'},
            {key: 2, name: 'Egg', unit: 'PIECE', amount: '0'},
            {key: 3, name: 'Milk', unit: 'LITRE', amount: '1.5'},
        ]);

        expect(errors.name).toBe('Name is required');
        expect(errors.ingredients[1]).toBe('Ingredient name is required');
        expect(errors.ingredients[2]).toBe('Quantity must be a whole number greater than 0');
        expect(errors.ingredients[3]).toBe('Quantity must be a whole number greater than 0');
    });
});
