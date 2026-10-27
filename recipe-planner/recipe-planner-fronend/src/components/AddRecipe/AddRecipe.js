import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import {Alert, Button, Col, Form, Row} from 'react-bootstrap';
import './AddRecipe.css';
import AddIngredient from "../AddIngredient/AddIngredient";
import {addRecipe} from "../../apis/recipeApi";

const EMPTY_RECIPE = {name: '', description: '', imageUrl: ''};

const newIngredient = (key) => ({key, name: '', unit: 'PIECE', amount: ''});

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

// wandelt die Formulardaten in das Format um, das das Backend erwartet
const toRequest = (recipe, ingredients) => ({
    ...recipe,
    name: recipe.name.trim(),
    ingredients: ingredients.map(({name, unit, amount}) => ({
        name: name.trim(),
        unit,
        amount: Number(amount),
    })),
});

function AddRecipe() {
    const navigate = useNavigate();
    const [recipe, setRecipe] = useState(EMPTY_RECIPE);
    const [ingredients, setIngredients] = useState([]);
    const [nextKey, setNextKey] = useState(1);
    const [errors, setErrors] = useState({ingredients: {}});
    const [submitError, setSubmitError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleChange = (field) => (event) => {
        setRecipe({...recipe, [field]: event.target.value});
    };

    const addIngredient = () => {
        setIngredients([...ingredients, newIngredient(nextKey)]);
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
            await addRecipe(toRequest(recipe, ingredients));
            navigate('/');
        } catch (error) {
            setSubmitError('The recipe could not be saved. Please try again.');
            setIsSubmitting(false);
        }
    };

    return (
        <div className="bg">
            <Form className="m-3" onSubmit={handleSubmit} noValidate>
                <h1 className="h3 bg-dark text-bg-primary mt-2">Add Recipe</h1>
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

export default AddRecipe;
