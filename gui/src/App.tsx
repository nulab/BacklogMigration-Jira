import React from "react";
import "./App.css";

const App: React.FC = () => {
  return (
    <div className="App">
      <form id="myForm" onSubmit={() => {}}>
        <fieldset>
          <div className="row">
            <label htmlFor="spaceID">
              {/* <?= getMessage("label_spaceId") ?> */}
            </label>
            <div className="space-id-field">
              <input
                id="spaceID"
                type="text"
                name="space"
                value="<?= config.space ?>"
                tabIndex={1}
                className="space-id-field__id"
              />
              <span className="space-id-field__name">.backlog</span>
              <select
                name="domain"
                tabIndex={2}
                className="space-id-field__domain"
              >
                {/* <? if (config.domain == ".com") { ?>
                 <option value=".com" selected>.com</option>
               <? } else  { ?>
                 <option value=".com">.com</option>
               <? } ?>
               <? if (config.domain == ".jp") { ?>
                 <option value=".jp" selected>.jp</option>
               <? } else  { ?>
                 <option value=".jp">.jp</option>
               <? } ?> */}
              </select>
            </div>
          </div>
          <div className="row">
            <label htmlFor="apiKey">
              {/* <?= getMessage("label_apiKey") ?> */}
            </label>
            <input
              id="apiKey"
              type="text"
              name="apiKey"
              value="<?= config.apiKey ?>"
              tabIndex={3}
            />
          </div>
          <div className="row">
            <label htmlFor="projectKey">
              {/* <?= getMessage("label_projectKey") ?> */}
            </label>
            <input
              id="projectKey"
              type="text"
              name="projectKey"
              value="<?= config.projectKey ?>"
              tabIndex={4}
            />
          </div>
          <div className="button-area">
            <input
              type="submit"
              className="button"
              value="<?= getMessage('button_execute') ?>"
              tabIndex={5}
            />
          </div>
        </fieldset>
      </form>
    </div>
  );
};

export default App;
