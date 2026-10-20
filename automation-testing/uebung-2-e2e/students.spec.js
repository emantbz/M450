// @ts-check
const { test, expect } = require('@playwright/test');

test.describe('TBZ Students App', () => {

  test('Startseite zeigt Logo und Navigation', async ({ page }) => {
    await page.goto('/');

    await expect(page).toHaveTitle('TBZ Students');
    await expect(page.getByAltText('Responsive image')).toBeVisible();
    await expect(page.getByRole('link', { name: 'List Students' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Add Students' })).toBeVisible();
  });

  test('Liste zeigt die Start-Studenten aus dem Backend', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('link', { name: 'List Students' }).click();

    await expect(page).toHaveURL(/\/students$/);
    for (const name of ['Jonas', 'Patrick', 'Yves', 'Peter', 'Ann']) {
      await expect(page.getByRole('cell', { name, exact: true })).toBeVisible();
    }
    await expect(page.getByRole('link', { name: 'jonas@tbz.ch' }))
      .toHaveAttribute('href', 'mailto:jonas@tbz.ch');
  });

  test('Formular: Submit ist erst aktiv, wenn alle Felder ausgefüllt sind', async ({ page }) => {
    await page.goto('/addstudents');
    const submit = page.getByRole('button', { name: 'Submit' });

    await expect(page.getByText('Name is required')).toBeVisible();
    await expect(page.getByText('Email is required')).toBeVisible();
    await expect(submit).toBeDisabled();

    await page.getByLabel('Name').fill('Nur Name');
    await expect(page.getByText('Name is required')).toBeHidden();
    await expect(submit).toBeDisabled();

    await page.getByLabel('Email').fill('nur@tbz.ch');
    await expect(submit).toBeEnabled();
  });

  test('Neuen Studenten erfassen und in der Liste sehen', async ({ page }) => {
    const name = `Playwright ${Date.now()}`;
    const email = `playwright${Date.now()}@tbz.ch`;

    await page.goto('/');
    await page.getByRole('link', { name: 'Add Students' }).click();
    await expect(page).toHaveURL(/\/addstudents$/);

    await page.getByLabel('Name').fill(name);
    await page.getByLabel('Email').fill(email);
    await page.getByRole('button', { name: 'Submit' }).click();

    // nach dem Speichern leitet die App auf die Liste weiter
    await expect(page).toHaveURL(/\/students$/);
    const row = page.getByRole('row').filter({ hasText: name });
    await expect(row).toBeVisible();
    await expect(row.getByRole('link', { name: email })).toBeVisible();
  });
});
