## Goal
"/invoices/:id" to show a form same as "/invoices-insert" that shows the fields filled with the actual data in order to update or cancel.

## Proposed changes
- add update endpoint and logic. make a view.
- need to spec what the amount field in the form.
- submitting the form should return "/invoices"

## PR status, roadblocks, etc
 - :id and :amount pass as form-params in "/invoices-update" and can be updated in the form yet id shouldn't be update as it is a SERIAL in the DB.
 
 
