// @ts-check
const { defineConfig, devices } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './e2e',
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://localhost:4200',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
  // Startet Backend und Frontend automatisch, falls sie noch nicht laufen
  webServer: [
    {
      command: './mvnw spring-boot:run',
      cwd: '..',
      url: 'http://localhost:8081/students',
      reuseExistingServer: true,
      timeout: 180_000,
    },
    {
      command: 'npx ng serve --port 4200',
      cwd: '../src/main/js/my-app',
      url: 'http://localhost:4200',
      reuseExistingServer: true,
      timeout: 180_000,
    },
  ],
});
