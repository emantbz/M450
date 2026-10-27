import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Alert, Button, Col, Form, Row} from 'react-bootstrap';
import '../AddRecipe/AddRecipe.css';
import AddIngredient from "../AddIngredient/AddIngredient";

const EMPTY_RECIPE = {name: '', description: '', imageUrl: '', ingredients: []};

// prüft die Eingaben und liefert ein Objekt mit Fehlermeldungen (leer = alles ok)
export const validate = (recipe, ingredients) => {
    const errors = {ingredients: {}};
    if (!recipe.name.trim()) {
        errors.name = 'Name is required';
    }
    ingredients.forEach((ingredient) => {
        if (!ingredient.name.trim()) {
            errors.ingredients[ingredient.key] = 'Ingredient name is required';
        } else if (!(Number(ingredient.amount) > 0) || !Number.isInteger(Number(ingredient.amount))) {
            errors.ingredients[ingredient.key] = 'Quantity must be a whole number greater than 0';
        }
    });
    return errors;
};

const hasErrors = (errors) => !!errors.name || Object.keys(errors.ingredients).length > 0;

// Zutaten aus dem Backend bekommen einen eindeutigen key fürs Formular
const toFormIngredients = (ingredients) => ingredients.map((ingredient, index) => ({
    key: index + 1,
    id: ingredient.id ?? null,
    comment: ingredient.comment ?? null,
    name: ingredient.name ?? '',
    unit: ingredient.unit ?? 'PIECE',
    amount: ingredient.amount ?? '',
}));

// wandelt die Formulardaten in das Format um, das das Backend erwartet
const toRequest = (recipe, ingredients) => ({
    name: recipe.name.trim(),
    description: recipe.description,
    imageUrl: recipe.imageUrl,
    ingredients: ingredients.map(({id, comment, name, unit, amount}) => ({
        ...(id && {id}),
        ...(comment && {comment}),
        name: name.trim(),
        unit,
        amount: Number(amount),
    })),
});

function RecipeForm({title, initialRecipe = EMPTY_RECIPE, onSubmit}) {
    const [recipe, setRecipe] = useState({
        name: initialRecipe.name ?? '',
        description: initialRecipe.description ?? '',
        imageUrl: initialRecipe.imageUrl ?? '',
    });
    const [ingredients, setIngredients] = useState(() => toFormIngredients(initialRecipe.ingredients ?? []));
    const [nextKey, setNextKey] = useState(ingredients.length + 1);
    const [errors, setErrors] = useState({ingredients: {}});
    const [submitError, setSubmitError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleChange = (field) => (event) => {
        setRecipe({...recipe, [field]: event.target.value});
    };

    const addIngredient = () => {
        setIngredients([...ingredients, {key: nextKey, id: null, comment: null, name: '', unit: 'PIECE', amount: ''}]);
        setNextKey(nextKey + 1);
    };

    const updateIngredient = (updated) => {
        setIngredients(ingredients.map((ingredient) => ingredient.key === updated.key ? updated : ingredient));
    };

    const removeIngredient = (removed) => {
        setIngredients(ingredients.filter((ingredient) => ingredient.key !== removed.key));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const validationErrors = validate(recipe, ingredients);
        setErrors(validationErrors);
        if (hasErrors(validationErrors)) {
            return;
        }

        setIsSubmitting(true);
        setSubmitError(null);
        try {
            await onSubmit(toRequest(recipe, ingredients));
        } catch (error) {
            setSubmitError('The recipe could not be saved. Please try again.');
            setIsSubmitting(false);
        }
    };

    return (
        <div className="bg">
            <Form className="m-3" onSubmit={handleSubmit} noValidate>
                <h1 className="h3 bg-dark text-bg-primary mt-2">{title}</h1>
                {submitError && <Alert variant="danger">{submitError}</Alert>}
                <Form.Group className="mb-1" controlId="formBasicName">
                    <Form.Label>Recipe Name:</Form.Label>
                    <Form.Control
                        placeholder="Name"
                        value={recipe.name}
                        onChange={handleChange('name')}
                        isInvalid={!!errors.name}
                    />
                    <Form.Control.Feedback type="invalid">{errors.name}</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className="mb-1" controlId="formBasicDescription">
                    <Form.Label>Description:</Form.Label>
                    <Form.Control
                        placeholder="Description"
                        value={recipe.description}
                        onChange={handleChange('description')}
                    />
                </Form.Group>
                <Form.Group className="mb-1 mb-5" controlId="formBasicImageUrl">
                    <Form.Label>Image URL:</Form.Label>
                    <Form.Control
                        type="url"
                        placeholder="URL"
                        value={recipe.imageUrl}
                        onChange={handleChange('imageUrl')}
                    />
                </Form.Group>
                <Row>
                    <Col>Ingredient</Col>
                    <Col>Unit</Col>
                    <Col>Quantity</Col>
                    <Col xs={1}></Col>
                </Row>
                <hr/>
                {ingredients.map((ingredient) => (
                    <AddIngredient
                        key={ingredient.key}
                        ingredient={ingredient}
                        error={errors.ingredients[ingredient.key]}
                        updateIngredient={updateIngredient}
                        removeIngredient={removeIngredient}
                    />
                ))}
                <Row>
                    <Button variant='warning' onClick={addIngredient} className="mt-1">
                        Add Ingredient
                    </Button>
                </Row>
                <Button variant="primary" type="submit" className="mb-5" disabled={isSubmitting}>
                    {isSubmitting ? 'Saving…' : 'Submit'}
                </Button>
            </Form>
        </div>
    )
}

RecipeForm.propTypes = {
    title: PropTypes.string.isRequired,
    initialRecipe: PropTypes.shape({
        name: PropTypes.string,
        description: PropTypes.string,
        imageUrl: PropTypes.string,
        ingredients: PropTypes.arrayOf(PropTypes.object),
    }),
    onSubmit: PropTypes.func.isRequired,
};

export default RecipeForm;
