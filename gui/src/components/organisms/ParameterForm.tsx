import React from 'react';
import styled from '@emotion/styled';
import { FieldSet } from '../atoms/FieldSet';
import { Button } from '../atoms/Button';
import { Row } from '../atoms/Row';
import { Label } from '../atoms/Label';
import { TextInput } from '../atoms/Input';
import { Select, Option } from '../atoms/Select';
import { Parameters } from '../../models/Prameter';

import { Formik, Field, Form, FormikActions } from 'formik';

const BacklogUrlFieldStyled = styled.div(`
  display: flex;
  align-items: center;
`);

const SpanStyled = styled.span(`
  flex: auto 0 0;
  padding: 0 10px;
`);

const ButtonArea = styled.div(`
  margin-top: 20px;
  text-align: center;
`);

const InputField = styled.div(`
  flex: auto 1 1;
`);

export const ParameterForm = () => (
  <Formik
    initialValues={{
      firstName: '',
      lastName: '',
      email: '',
    }}
    onSubmit={(
      values: Parameters,
      { setSubmitting }: FormikActions<Parameters>
    ) => {
      setTimeout(() => {
        alert(JSON.stringify(values, null, 2));
        setSubmitting(false);
      }, 500);
    }}
    render={() => (
      <Form>
        <FieldSet>
          <Row>
            <Label htmlFor='backlogUrl'>Backlog URL</Label>
            <BacklogUrlFieldStyled>
              <SpanStyled>https://</SpanStyled>
              <InputField>
                <TextInput
                  id='backlogUrl'
                  name='spaceId'
                  value=''
                  placeholder='YOUR SPACE ID'
                  tabIndex={1}
                />
              </InputField>
              <Select name='domain' tabIndex={2}>
                <Option value='.backlog.com' />
                <Option value='.backlog.jp' />
                <Option value='.backlogtool.com' />
              </Select>
            </BacklogUrlFieldStyled>
          </Row>
          <Row>
            <Label htmlFor='apiKey'>API Key</Label>
            <TextInput
              id='apiKey'
              name='apiKey'
              value=''
              placeholder='INPUT YOUR API KEY'
              tabIndex={3}
            />
          </Row>
          <Row>
            <Label htmlFor='projectKey'>Project Key</Label>
            <TextInput
              id='projectKey'
              name='projectKey'
              value=''
              placeholder='INPUT YOUR PROJECT KEY'
              tabIndex={4}
            />
          </Row>
          <ButtonArea>
            <Button type='submit' value='Execute' tabIndex={5} />
          </ButtonArea>
        </FieldSet>
      </Form>
    )}
  />
);
